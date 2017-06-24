package com.bhate.cet.allotment;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.springframework.stereotype.Service;

/**
 * Created by pb on 18/06/2017.
 */
@WebService
@Service
public class ApplicationWebservice {
	@WebMethod(operationName = "dummyOperation")
	MyResponse mySoapService(MyRequest request) {
		return new MyResponse(request.getId(),request.getName(),"details");
	}
}

class MyResponse {
	public MyResponse(int id, String name, String details) {
		this.id = id;
		this.name = name;
		this.details = details;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	private int id;
	private String name;
	private String details;
}

class MyRequest {
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private int id;
	private String name;

}
