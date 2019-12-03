package com.yoyo.consul.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RestController
public class ServiceController {
	@Autowired
	private LoadBalancerClient loadBalancer;
	@Autowired
	private DiscoveryClient discoveryClient;
	
	@Autowired
	private RestTemplate restTemplate;

	private Logger logger = LoggerFactory.getLogger(getClass());

	@GetMapping("/service")
	public Object services() {
		try {
			Object object = discoveryClient.getInstances("yoyoconsul-p1");
			logger.debug(object.toString());

			String uri = this.loadBalancer.choose("yoyoconsul-p1").getUri().toString();
			logger.debug(uri);

			return object;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("",e);
		}
		return null;
	}

	@GetMapping("/callHello")
	public String callHello() {
		try {
			ServiceInstance serviceInstance = this.loadBalancer.choose("yoyoconsul-p1");
			
			String uri = serviceInstance.getUri().toString();
			logger.debug("service uri is:" + uri);
			String serviceName = serviceInstance.getInstanceId();
			logger.debug("service id is:" + serviceName);
			String responseStr = this.restTemplate.getForObject(uri + "/test", String.class); 
			return responseStr;
		} catch (RestClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("",e);
		}
		return "";
	}

}
