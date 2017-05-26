package edu.iris.dmc.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import edu.iris.dmc.criteria.Criteria;
import edu.iris.dmc.criteria.CriteriaException;

public interface IrisService<T> {

	public List<T> fetch(Criteria criteria) throws IOException, CriteriaException, NoDataFoundException;

	public List<T> fetch(String url) throws IOException, CriteriaException, NoDataFoundException;

	public List<T> fetch(String url, String username, String password)
			throws IOException, NoDataFoundException, CriteriaException;

	public List<T> fetch(String url, String username, String password, ResponseHandler<T> handler)
			throws IOException, CriteriaException, NoDataFoundException;

	public void stream(String url, OutputStream out) throws NoDataFoundException, CriteriaException, IOException;

	public List<T> load(InputStream inputStream, ResponseHandler<T> handler) throws IOException;
}
