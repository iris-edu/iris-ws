package edu.iris.dmc.service.response;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import edu.iris.dmc.service.ResponseHandler;
import edu.iris.dmc.ws.util.StringUtil;

public class RespResponseHandler implements ResponseHandler<String> {

	@Override
	public List<String> handle(InputStream inputStream) {
		List<String> list = new ArrayList<String>();
		String message = StringUtil.toString(inputStream);
		list.add(message);
		return list;
	}
	@Override
	public String getContentType() {
		return "application/text";
	}
}
