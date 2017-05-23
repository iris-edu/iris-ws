package edu.iris.dmc.seedcodec;

/**
 * DecompressedData.java
 *
 *
 * Created: Thu Nov 21 13:03:44 2002
 *
 * @author <a href="mailto:crotwell@seis.sc.edu">Philip Crotwell</a>
 * @version 1.0.5
 */

public class DecompressedData implements B1000Types {

	private int size;
	// private short[] shorts;
	// private int[] ints;
	private float[] floats;

	private float min;
	private float max;

	private DecompressedData(float[] floats) {
		this.floats = floats;

		if (this.floats != null) {
			this.size = this.floats.length;
		}
	}

	public int size() {
		return this.size;
	}

	public float[] getData() {
		return floats;
	}

	public float getMin() {
		return this.min;
	}

	public float getMax() {
		return this.max;
	}

	public static DecompressedData of(Type type, byte[] b, int numSamples, boolean reduce, boolean swapBytes)
			throws CodecException {
		int offset = 0;
		float min = Integer.MAX_VALUE;
		float max = Integer.MIN_VALUE;
		// DecompressedData dData = null;
		float[] array = new float[numSamples];
		switch (type) {
		case SHORT:
		case DWWSSN:
			if (b.length < 2 * numSamples) {
				throw new CodecException(
						"Not enough bytes for " + numSamples + " 16 bit data points, only " + b.length + " bytes.");
			}

			for (int i = 0; i < numSamples; i++) {
				array[i] = Utility.bytesToShort(b[offset], b[offset + 1], swapBytes);
				if (array[i] < min) {
					min = array[i];
				}
				if (array[i] > max) {
					max = array[i];
				}
				offset += 2;
			}
			break;
		case INT24:
			// 24 bit values
			if (b.length < 3 * numSamples) {
				throw new CodecException(
						"Not enough bytes for " + numSamples + " 24 bit data points, only " + b.length + " bytes.");
			}
			// Integer[] itemp = new Integer[numSamples];
			for (int i = 0; i < numSamples; i++) {
				array[i] = Utility.bytesToInt(b[offset], b[offset + 1], b[offset + 2], swapBytes);
				offset += 3;
				if (array[i] < min) {
					min = array[i];
				}
				if (array[i] > max) {
					max = array[i];
				}
			}
			break;
		case INTEGER:
			// 32 bit integers
			if (b.length < 4 * numSamples) {
				throw new CodecException(
						"Not enough bytes for " + numSamples + " 32 bit data points, only " + b.length + " bytes.");
			}
			for (int i = 0; i < numSamples; i++) {
				array[i] = Utility.bytesToInt(b[offset], b[offset + 1], b[offset + 2], b[offset + 3], swapBytes);
				offset += 4;
				if (array[i] < min) {
					min = array[i];
				}
				if (array[i] > max) {
					max = array[i];
				}
			}
			break;
		case FLOAT:
			// 32 bit floats
			if (b.length < 4 * numSamples) {
				throw new CodecException(
						"Not enough bytes for " + numSamples + " 32 bit data points, only " + b.length + " bytes.");
			}
			for (int i = 0; i < numSamples; i++) {
				array[i] = Float.intBitsToFloat(
						Utility.bytesToInt(b[offset], b[offset + 1], b[offset + 2], b[offset + 3], swapBytes));
				offset += 4;
				if (array[i] < min) {
					min = (int) array[i];
				}
				if (array[i] > max) {
					max = (int) array[i];
				}
			}
			break;
		case DOUBLE:
			// 64 bit doubles
			if (b.length < 8 * numSamples) {
				throw new CodecException(
						"Not enough bytes for " + numSamples + " 64 bit data points, only " + b.length + " bytes.");
			}
			// ToDo .. implement this type....
			throw new UnsupportedCompressionType("Type " + type + " is not supported at this time.");
			// break;
		case STEIM1:
			// steim 1
			DecompressedStruct struct = Steim1.decode(b, numSamples, false, 0); // swapBytes
			// field
			// always
			// false
			// for
			// Steim
			// Blocks
			array = struct.getData();
			min = struct.getMin();
			max = struct.getMax();
			break;
		case STEIM2:
			// steim 2
			struct = Steim2.decode(b, numSamples, false, 0);
			array = struct.getData();
			min = struct.getMin();
			max = struct.getMax();
			break;

		case CDSN:
			array = Cdsn.decode(b, numSamples, swapBytes);
			for (float num : array) {
				if (num < min) {
					min = num;
				}
				if (num > max) {
					max = num;
				}
			}
			break;
		case SRO:
			array = Sro.decode(b, numSamples, swapBytes);
			for (float num : array) {
				if (num < min) {
					min = num;
				}
				if (num > max) {
					max = num;
				}
			}
			break;
		default:
			// unknown format????
			throw new UnsupportedCompressionType("Type " + type + " is not supported at this time.");
		} // end of switch ()
		DecompressedData dData = null;
		if (reduce) {
			float[] reducedArray = ramerDouglasPeuckerFunction(array, 0, array.length - 1, 1);
			dData = new DecompressedData(reducedArray);
			for (float num : array) {
				if (num < min) {
					min = num;
				}
				if (num > max) {
					max = num;
				}
			}
		} else {
			dData = new DecompressedData(array);
		}
		dData.min = min;
		dData.max = max;
		return dData;
	}

	protected static float[] ramerDouglasPeuckerFunction(float[] points, int startIndex, int endIndex, double epsilon) {
		if (epsilon <= 0) {
			throw new IllegalArgumentException("Epsilon nust be > 0");
		}
		double dmax = 0;
		int idx = 0;
		double a = endIndex - startIndex;
		double b = points[endIndex] - points[startIndex];
		double c = -(b * startIndex - a * points[startIndex]);
		double norm = Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
		for (int i = startIndex + 1; i < endIndex; i++) {
			double distance = Math.abs(b * i - a * points[i] + c) / norm;
			if (distance > dmax) {
				idx = i;
				dmax = distance;
			}
		}
		if (dmax >= epsilon) {
			float[] recursiveResult1 = ramerDouglasPeuckerFunction(points, startIndex, idx, epsilon);
			float[] recursiveResult2 = ramerDouglasPeuckerFunction(points, idx, endIndex, epsilon);
			float[] result = new float[(recursiveResult1.length - 1) + recursiveResult2.length];
			System.arraycopy(recursiveResult1, 0, result, 0, recursiveResult1.length - 1);
			System.arraycopy(recursiveResult2, 0, result, recursiveResult1.length - 1, recursiveResult2.length);
			return result;
		} else {
			return new float[] { points[startIndex], points[endIndex] };
		}
	}

}// DecompressedData
