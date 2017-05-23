package edu.iris.dmc.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface ResponseHandler<T> {

	public String getContentType();

	public List<T> handle(InputStream inputStream) throws IOException;
}
