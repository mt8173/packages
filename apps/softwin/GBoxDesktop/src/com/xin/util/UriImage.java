package com.xin.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.provider.Telephony.Mms.Part;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class UriImage {
    private static final String TAG = "UriImage";
    private static final boolean LOCAL_LOGV = false;
    private static final int MMS_PART_ID = 12;
    /**
     * The quality parameter which is used to compress JPEG images.
     */
    public static final int IMAGE_COMPRESSION_QUALITY = 95;
    /**
     * The minimum quality parameter which is used to compress JPEG images.
     */
    public static final int MINIMUM_IMAGE_COMPRESSION_QUALITY = 50;

    private static final UriMatcher sURLMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURLMatcher.addURI("mms", "part/#", MMS_PART_ID);
    }

    private final Context mContext;
    private final Uri mUri;
    private String mContentType;
    private String mPath;
    private String mSrc;
    private int mWidth;
    private int mHeight;

    public UriImage(Context context, Uri uri) {
        if ((null == context) || (null == uri)) {
            throw new IllegalArgumentException();
        }

        String scheme = uri.getScheme();
        if (scheme.equals("content")) {
            initFromContentUri(context, uri);
        } else if (uri.getScheme().equals("file")) {
            initFromFile(context, uri);
        }

        mContext = context;
        mUri = uri;

        decodeBoundsInfo();

        if (LOCAL_LOGV) {
            Log.v(TAG, "UriImage uri: " + uri + " mPath: " + mPath + " mWidth: " + mWidth +
                    " mHeight: " + mHeight);
        }
    }

    private void initFromFile(Context context, Uri uri) {
        mPath = uri.getPath();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String extension = MimeTypeMap.getFileExtensionFromUrl(mPath);
        if (TextUtils.isEmpty(extension)) {
            // getMimeTypeFromExtension() doesn't handle spaces in filenames nor can it handle
            // urlEncoded strings. Let's try one last time at finding the extension.
            int dotPos = mPath.lastIndexOf('.');
            if (0 <= dotPos) {
                extension = mPath.substring(dotPos + 1);
            }
        }
        mContentType = mimeTypeMap.getMimeTypeFromExtension(extension);
        // It's ok if mContentType is null. Eventually we'll show a toast telling the
        // user the picture couldn't be attached.

        buildSrcFromPath();
    }

    private void buildSrcFromPath() {
        mSrc = mPath.substring(mPath.lastIndexOf('/') + 1);

        if(mSrc.startsWith(".") && mSrc.length() > 1) {
            mSrc = mSrc.substring(1);
        }

        // Some MMSCs appear to have problems with filenames
        // containing a space.  So just replace them with
        // underscores in the name, which is typically not
        // visible to the user anyway.
        mSrc = mSrc.replace(' ', '_');
    }

    private void initFromContentUri(Context context, Uri uri) {
        ContentResolver resolver = context.getContentResolver();
        //Cursor c = SqliteWrapper.query(context, resolver,uri, null, null, null, null);
        Cursor c=resolver.query(uri, null, null, null, null);
        mSrc = null;
        if (c == null) {
            throw new IllegalArgumentException(
                    "Query on " + uri + " returns null result.");
        }

        try {
            if ((c.getCount() != 1) || !c.moveToFirst()) {
                throw new IllegalArgumentException(
                        "Query on " + uri + " returns 0 or multiple rows.");
            }

            String filePath;
            if (isMmsUri(uri)) {
                filePath = c.getString(c.getColumnIndexOrThrow(Part.FILENAME));
                if (TextUtils.isEmpty(filePath)) {
                    filePath = c.getString(
                            c.getColumnIndexOrThrow(Part._DATA));
                }
                mContentType = c.getString(
                        c.getColumnIndexOrThrow(Part.CONTENT_TYPE));
            } else {
                filePath = uri.getPath();
                try {
                    mContentType = c.getString(
                            c.getColumnIndexOrThrow(Images.Media.MIME_TYPE)); // mime_type
                } catch (IllegalArgumentException e) {
                    try {
                        mContentType = c.getString(c.getColumnIndexOrThrow("mimetype"));
                    } catch (IllegalArgumentException ex) {
                        mContentType = resolver.getType(uri);
                        Log.v(TAG, "initFromContentUri: " + uri + ", getType => " + mContentType);
                    }
                }

                // use the original filename if possible
                int nameIndex = c.getColumnIndex(Images.Media.DISPLAY_NAME);
                if (nameIndex != -1) {
                    mSrc = c.getString(nameIndex);
                    if (!TextUtils.isEmpty(mSrc)) {
                        // Some MMSCs appear to have problems with filenames
                        // containing a space.  So just replace them with
                        // underscores in the name, which is typically not
                        // visible to the user anyway.
                        mSrc = mSrc.replace(' ', '_');
                    } else {
                        mSrc = null;
                    }
                }
            }
            mPath = filePath;
            if (mSrc == null) {
                buildSrcFromPath();
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "initFromContentUri couldn't load image uri: " + uri, e);
        } finally {
            c.close();
        }
    }
    public static boolean isMmsUri(Uri uri) {
        return uri.getAuthority().startsWith("mms");
    }

    private void decodeBoundsInfo() {
        InputStream input = null;
        try {
            input = mContext.getContentResolver().openInputStream(mUri);
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, opt);
            mWidth = opt.outWidth;
            mHeight = opt.outHeight;
        } catch (FileNotFoundException e) {
            // Ignore
            Log.e(TAG, "IOException caught while opening stream", e);
        } finally {
            if (null != input) {
                try {
                    input.close();
                } catch (IOException e) {
                    // Ignore
                    Log.e(TAG, "IOException caught while closing stream", e);
                }
            }
        }
    }

    public String getContentType() {
        return mContentType;
    }

    public String getSrc() {
        return mSrc;
    }

    public String getPath() {
        return mPath;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }


    public Bitmap getResizedImage(int widthLimit, int heightLimit, int byteLimit) {
        byte[] data =  getResizedImageData(mWidth, mHeight,
                widthLimit, heightLimit, byteLimit,0, mUri, mContext);
        if (data == null) {
            if (LOCAL_LOGV) {
                Log.v(TAG, "Resize image failed.");
            }
            return null;
        }
        Bitmap bitmap=BitmapFactory.decodeByteArray(data, 0, data.length);
        return bitmap;
    }


    private static final int NUMBER_OF_RESIZE_ATTEMPTS = 4;

    /**
     * Resize and recompress the image such that it fits the given limits. The resulting byte
     * array contains an image in JPEG format, regardless of the original image's content type.
     * @param widthLimit The width limit, in pixels
     * @param heightLimit The height limit, in pixels
     * @param byteLimit The binary size limit, in bytes
     * @return A resized/recompressed version of this image, in JPEG format
     */
    public static byte[] getResizedImageData(int width, int height,
            int widthLimit, int heightLimit, int byteLimit,int orientation, Uri uri, Context context) {
        int outWidth = width;
        int outHeight = height;

        float scaleFactor = 1.F;
        while ((outWidth * scaleFactor > widthLimit) || (outHeight * scaleFactor > heightLimit)) {
            scaleFactor *= .75F;
        }

        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "getResizedBitmap: wlimit=" + widthLimit +
                    ", hlimit=" + heightLimit + ", sizeLimit=" + byteLimit +
                    ", width=" + width + ", height=" + height +
                    ", initialScaleFactor=" + scaleFactor +
                    ", uri=" + uri +
                    ", orientation=" + orientation);
        }

        InputStream input = null;
        ByteArrayOutputStream os = null;
        try {
            int attempts = 1;
            int sampleSize = 1;
            BitmapFactory.Options options = new BitmapFactory.Options();
            int quality = IMAGE_COMPRESSION_QUALITY;
            Bitmap b = null;

            // In this loop, attempt to decode the stream with the best possible subsampling (we
            // start with 1, which means no subsampling - get the original content) without running
            // out of memory.
            do {
                input = context.getContentResolver().openInputStream(uri);
                options.inSampleSize = sampleSize;
                try {
                    b = BitmapFactory.decodeStream(input, null, options);
                    if (b == null) {
                        return null;    // Couldn't decode and it wasn't because of an exception,
                                        // bail.
                    }
                } catch (OutOfMemoryError e) {
                    Log.w(TAG, "getResizedBitmap: img too large to decode (OutOfMemoryError), " +
                            "may try with larger sampleSize. Curr sampleSize=" + sampleSize);
                    sampleSize *= 2;    // works best as a power of two
                    attempts++;
                    continue;
                } finally {
                    if (input != null) {
                        try {
                            input.close();
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage(), e);
                        }
                    }
                }
            } while (b == null && attempts < NUMBER_OF_RESIZE_ATTEMPTS);

            if (b == null) {
                if (Log.isLoggable(TAG, Log.VERBOSE)
                        && attempts >= NUMBER_OF_RESIZE_ATTEMPTS) {
                    Log.v(TAG, "getResizedImageData: gave up after too many attempts to resize");
                }
                return null;
            }

            boolean resultTooBig = true;
            attempts = 1;   // reset count for second loop
            // In this loop, we attempt to compress/resize the content to fit the given dimension
            // and file-size limits.
            do {
                try {
                    if (options.outWidth > widthLimit || options.outHeight > heightLimit ||
                            (os != null && os.size() > byteLimit)) {
                        // The decoder does not support the inSampleSize option.
                        // Scale the bitmap using Bitmap library.
                        int scaledWidth = (int)(outWidth * scaleFactor);
                        int scaledHeight = (int)(outHeight * scaleFactor);

                        if (Log.isLoggable(TAG, Log.VERBOSE)) {
                            Log.v(TAG, "getResizedImageData: retry scaling using " +
                                    "Bitmap.createScaledBitmap: w=" + scaledWidth +
                                    ", h=" + scaledHeight);
                        }

                        b = Bitmap.createScaledBitmap(b, scaledWidth, scaledHeight, false);
                        if (b == null) {
                            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                                Log.v(TAG, "Bitmap.createScaledBitmap returned NULL!");
                            }
                            return null;
                        }
                    }

                    // Compress the image into a JPG. Start with MessageUtils.IMAGE_COMPRESSION_QUALITY.
                    // In case that the image byte size is still too large reduce the quality in
                    // proportion to the desired byte size.
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage(), e);
                        }
                    }
                    os = new ByteArrayOutputStream();
                    b.compress(CompressFormat.JPEG, quality, os);
                    int jpgFileSize = os.size();
                    if (jpgFileSize > byteLimit) {
                        quality = (quality * byteLimit) / jpgFileSize;  // watch for int division!
                        if (quality < MINIMUM_IMAGE_COMPRESSION_QUALITY) {
                            quality = MINIMUM_IMAGE_COMPRESSION_QUALITY;
                        }

                        if (Log.isLoggable(TAG, Log.VERBOSE)) {
                            Log.v(TAG, "getResizedImageData: compress(2) w/ quality=" + quality);
                        }

                        if (os != null) {
                            try {
                                os.close();
                            } catch (IOException e) {
                                Log.e(TAG, e.getMessage(), e);
                            }
                        }
                        os = new ByteArrayOutputStream();
                        b.compress(CompressFormat.JPEG, quality, os);
                    }
                } catch (java.lang.OutOfMemoryError e) {
                    Log.w(TAG, "getResizedImageData - image too big (OutOfMemoryError), will try "
                            + " with smaller scale factor, cur scale factor: " + scaleFactor);
                    // fall through and keep trying with a smaller scale factor.
                }
                if (Log.isLoggable(TAG, Log.VERBOSE)) {
                    Log.v(TAG, "attempt=" + attempts
                            + " size=" + (os == null ? 0 : os.size())
                            + " width=" + outWidth * scaleFactor
                            + " height=" + outHeight * scaleFactor
                            + " scaleFactor=" + scaleFactor
                            + " quality=" + quality);
                }
                scaleFactor *= .75F;
                attempts++;
                resultTooBig = os == null || os.size() > byteLimit;
            } while (resultTooBig && attempts < NUMBER_OF_RESIZE_ATTEMPTS);
            if (!resultTooBig && orientation != 0) {
                // Rotate the final bitmap if we need to.
                try {
                    b = UriImage.rotateBitmap(b, orientation);
                    os = new ByteArrayOutputStream();
                    b.compress(CompressFormat.JPEG, quality, os);
                    resultTooBig = os == null || os.size() > byteLimit;
                } catch (java.lang.OutOfMemoryError e) {
                    Log.w(TAG, "getResizedImageData - image too big (OutOfMemoryError)");
                    if (os == null) {
                        return null;
                    }
                }
            }

            b.recycle();        // done with the bitmap, release the memory
            if (Log.isLoggable(TAG, Log.VERBOSE) && resultTooBig) {
                Log.v(TAG, "getResizedImageData returning NULL because the result is too big: " +
                        " requested max: " + byteLimit + " actual: " + os.size());
            }

            return resultTooBig ? null : os.toByteArray();
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        } catch (java.lang.OutOfMemoryError e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Bitmap rotation method
     *
     * @param bitmap The input bitmap
     * @param degrees The rotation angle
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            final Matrix m = new Matrix();
            final int w = bitmap.getWidth();
            final int h = bitmap.getHeight();
            m.setRotate(degrees, (float) w / 2, (float) h / 2);

            try {
                final Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, m, true);
                if (bitmap != rotatedBitmap && rotatedBitmap != null) {
                    bitmap.recycle();
                    bitmap = rotatedBitmap;
                }
            } catch (OutOfMemoryError ex) {
                Log.e(TAG, "OOM in rotateBitmap", ex);
                // We have no memory to rotate. Return the original bitmap.
            }
        }

        return bitmap;
    }

}
