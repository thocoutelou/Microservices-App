package com.withBoardManager.boardManager;


/* a class to share the response of the HTTP server to all the component of the Board manager*/

public class HttpResponse {
	
	private String response="";
	private boolean isNew=false;

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
