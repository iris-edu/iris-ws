package edu.iris.dmc.service.response;

import java.io.InputStream;
import java.util.List;

import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.service.ResponseHandler;

public class StationTextResponseHandler implements ResponseHandler<Network>{

	@Override
	public List<Network> handle(InputStream inputStream) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getContentType() {
		return "application/text";
	}
}
