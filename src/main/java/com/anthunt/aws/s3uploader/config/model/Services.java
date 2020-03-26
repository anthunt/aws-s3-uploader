package com.anthunt.aws.s3uploader.config.model;

import java.util.ArrayList;
import java.util.List;

public class Services {

	private List<Service> services;
	
	public Services() {
		this.services = new ArrayList<Service>();
	}

	public List<Service> getServices() {
		return services;
	}

	public void setServices(List<Service> services) {
		this.services = services;
	}
	
}
