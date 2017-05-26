package edu.iris.dmc.service.response;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class PngEncoder {

	private byte[] png;
	private BufferedImage image;
	private final FilterType filter;

	private int width;
	private int height;

	private final byte[] IDAT = { 73, 68, 65, 84 };
	private final byte[] IEND = new byte[] { 73, 69, 78, 68 };
	private final byte[] IHDR = new byte[] { 73, 72, 68, 82 };
	private CRC32 crc32 = new CRC32();

	public PngEncoder(BufferedImage image) {
		this(image, FilterType.NONE);
	}

	public PngEncoder(BufferedImage image, FilterType filter) {
		this.image = image;
		this.filter = filter;
	}

	public byte[] run() throws IOException {
		if (this.image == null) {
			return null;
		}
		this.width = this.image.getWidth(null);
		this.height = this.image.getHeight(null);
		this.png = new byte[((this.width + 1) * this.height * 3) + 200];

		int index = 0;
		index = this.signature(index);
		index = this.idhr(index);
		index = this.idat(index);
		iend(index);
		return this.png;
	}

	protected byte[] to4Bytes(int n) {
		byte[] temp = { (byte) ((n >> 24) & 0xff), (byte) ((n >> 16) & 0xff), (byte) ((n >> 8) & 0xff),
				(byte) (n & 0xff) };
		return temp;
	}

	protected byte[] to1Byte(int b) {
		return new byte[] { (byte) b };
	}

	protected int writeBytesToPng(byte[] data, int offset) {
		if (data.length + offset > this.png.length) {
			this.png = resizeByteArray(this.png, this.png.length + Math.max(1000, data.length));
		}
		System.arraycopy(data, 0, this.png, offset, data.length);
		System.out.println("index now is: " + (offset + data.length));
		return offset + data.length;
	}

	protected byte[] resizeByteArray(byte[] array, int newLength) {
		byte[] newArray = new byte[newLength];
		int oldLength = array.length;

		System.arraycopy(array, 0, newArray, 0, Math.min(oldLength, newLength));
		return newArray;
	}

	// 5.2 PNG signature
	// The first eight bytes of a PNG datastream always contain the following
	// (decimal) values:
	// 137 80 78 71 13 10 26 10
	// This signature indicates that the remainder of the datastream contains a
	// single PNG image, consisting of a series of chunks beginning with an IHDR
	// chunk and ending with an IEND chunk.
	private int signature(int index) {
		// 137 >> -119
		byte[] bytes = new byte[] { -119, 80, 78, 71, 13, 10, 26, 10 };
		return writeBytesToPng(bytes, index);
	}

	// 11.2.2 IHDR Image header
	// The four-byte chunk type field contains the decimal values
	// 73 72 68 82
	// IHDR: image header, which is the first chunk in a PNG datastream.

	// Width 4 bytes
	// Height 4 bytes
	// Bit depth 1 byte
	// Colour type 1 byte
	// Compression method 1 byte
	// Filter method 1 byte
	// Interlace method 1 byte

	private int idhr(int index) {
		index = this.writeBytesToPng(this.to4Bytes(13), index);// length
		int start = index;
		index = this.writeBytesToPng(IHDR, index);
		index = this.writeBytesToPng(this.to4Bytes(this.image.getWidth(null)), index);
		index = this.writeBytesToPng(this.to4Bytes(this.image.getHeight(null)), index);

		// DEPTH: Each pixel is an R,G,B triple
		index = this.writeBytesToPng(this.to1Byte(8), index);

		// TRUE COLOR:
		index = this.writeBytesToPng(this.to1Byte(2), index);// No alpha
		// Only compression method 0 (deflate/inflate compression with a sliding
		// window of at most 32768 bytes)
		// is defined in this International Standard. All conforming PNG images
		// shall be compressed with this scheme.
		index = this.writeBytesToPng(this.to1Byte(0), index);
		// Filter method is a single-byte integer that indicates the
		// preprocessing method applied to the image
		// data before compression. Only filter method 0 (adaptive filtering
		// with five basic filter types) is
		// defined in this International Standard
		index = this.writeBytesToPng(this.to1Byte(this.filter.getValue()), index);

		// Interlace method is a single-byte integer that indicates the
		// transmission order of the image data.
		// Two values are defined in this International Standard: 0 (no
		// interlace) or 1 (Adam7 interlace).
		index = this.writeBytesToPng(this.to1Byte(0), index); // no interlace

		this.crc32.reset();
		this.crc32.update(this.png, start, index - start);
		index = writeBytesToPng(this.to4Bytes((int) this.crc32.getValue()), index);

		return index;
	}

	// PLTE: palette table associated with indexed PNG images.
	private void plte() {

	}

	// |LENGTH | TYPE | DATA | CRC|
	// IDAT: image data chunks.
	private int idat(int index) throws IOException {
		// ByteArrayOutputStream compressed = new ByteArrayOutputStream(65536);
		// BufferedOutputStream bos = new BufferedOutputStream(new
		// DeflaterOutputStream(compressed, new Deflater(9)));
		int rowsLeft = this.height; // number of rows remaining to write
		int startRow = 0; // starting row to process this time through
		int nRows; // how many rows to grab at a time

		byte[] scanLines; // the scan lines to be compressed
		int scanPos; // where we are in the scan lines
		

		byte[] compressedLines; // the resultant compressed lines

		// int depth; // color depth ( handle only 8 or 32 )

		PixelGrabber pg;
		int compressionLevel = 0;
		Deflater scrunch = new Deflater(compressionLevel);
		ByteArrayOutputStream outBytes = new ByteArrayOutputStream(1024);

		DeflaterOutputStream compBytes = new DeflaterOutputStream(outBytes, scrunch);
		int bytesPerPixel = 3;

		 // where this line's actual pixels start (used for filtering)
		int startPos;
		while (rowsLeft > 0) {
			nRows = Math.min(32767 / (this.width * (bytesPerPixel + 1)), rowsLeft);
			nRows = Math.max(nRows, 1);

			int[] pixels = new int[this.width * nRows];

			pg = new PixelGrabber(this.image, 0, startRow, this.width, nRows, pixels, 0, this.width);
			try {
				pg.grabPixels();
			} catch (Exception e) {
				System.err.println("interrupted waiting for pixels!");
			}
			if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
				System.err.println("image fetch aborted or errored");
			}

			/*
			 * Create a data chunk. scanLines adds "nRows" for the filter bytes.
			 */
			scanLines = new byte[this.width * nRows * bytesPerPixel + nRows];
			scanPos = 0;
			startPos = 1;
			for (int i = 0; i < this.width * nRows; i++) {
				if (i % this.width == 0) {
					scanLines[scanPos++] = (byte) filter.getValue();
					startPos = scanPos;
				}
				scanLines[scanPos++] = (byte) ((pixels[i] >> 16) & 0xff);
				scanLines[scanPos++] = (byte) ((pixels[i] >> 8) & 0xff);
				scanLines[scanPos++] = (byte) ((pixels[i]) & 0xff);
			}
			compBytes.write(scanLines, 0, scanPos);

			startRow += nRows;
			rowsLeft -= nRows;
		}
		compBytes.close();
		compressedLines = outBytes.toByteArray();

		this.crc32.reset();
		index = this.writeBytesToPng(this.to4Bytes(compressedLines.length), index);// length
		index = writeBytesToPng(IDAT, index);// type
		this.crc32.update(IDAT);
		index = writeBytesToPng(compressedLines, index);
		this.crc32.update(compressedLines, 0, compressedLines.length);

		int crcValue = (int) this.crc32.getValue();
		index = writeBytesToPng(this.to4Bytes(crcValue), index);// crc
		scrunch.finish();
		scrunch.end();
		return index;
	}

	// The four-byte chunk type field contains the decimal values
	// 73 69 78 68
	// The IEND chunk marks the end of the PNG datastream. The chunk's data
	// field is empty.
	private int iend(int index) {
		// Length is 0 per documentation
		index = writeBytesToPng(to4Bytes(0), index);
		index = writeBytesToPng(IEND, index);// TYPE
		this.crc32.reset();
		this.crc32.update(IEND);
		int crc = (int) this.crc32.getValue();
		index = writeBytesToPng(to4Bytes(crc), index);
		return index;
	}
}
