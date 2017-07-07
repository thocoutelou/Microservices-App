package com.withBoardManager.boardManager;

/* a class to share the response of the HTTP server to all the component of the Board manager*/

public class HttpResponse {

	private String response = "";
	private int count;
	private boolean isNew = false;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

}
