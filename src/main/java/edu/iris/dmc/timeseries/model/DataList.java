package edu.iris.dmc.timeseries.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DataList {

	private long startTime;
	private long endTime;
	private long expectedNextSampleTime;
	private List<DecompressedDataRecord> list;

	public DataList(int size) {
		this.list = new ArrayList<>(size);
	}

	public int add(DecompressedDataRecord data) {
		if (this.list.isEmpty()) {
			list.add(data);
			this.startTime = data.getStartTime();
			this.endTime = data.getEndTime();
			this.expectedNextSampleTime = data.getExpectedNextSampleTime();
			return 0;
		} else {
			// let us try to add to end, chuncks come in ordered (mostly)
			// this needs attention
			if (data.getStartTime() >= this.endTime) {
				this.list.add(data);
				this.endTime = data.getEndTime();
				this.expectedNextSampleTime = data.getExpectedNextSampleTime();
				return this.list.size() - 1;
			} else if (data.getEndTime() <= this.startTime) {
				this.list.add(0, data);
				this.startTime = data.getStartTime();
				return 0;
			} else {
				// so we know its out of order
				if (this.list.size() < 2) {
					return -1;
				}
				int middle = this.list.size() / 2;
				int index = this.add(data, 0, middle);
				if (index >= 0) {
					this.list.add(index, data);
					return index;
				}
				index = this.add(data, middle + 1, this.list.size() - 1);
				if (index >= 0) {
					this.list.add(index, data);
					return index;
				}
				return -1;
			}
		}
	}

	private int add(DecompressedDataRecord data, int start, int end) {
		if (end <= start) {

		}
		int middle = this.list.size() / 2;
		return 0;
	}

	public List<DecompressedDataRecord> get() {
		return this.list;
	}

}
