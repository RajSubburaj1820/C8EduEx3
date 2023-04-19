package io.camunda.getstarted;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.camunda.zeebe.spring.client.ZeebeClientLifecycle;

@RestController
@RequestMapping("/foo")
class InteractController {

	@Autowired
	private ZeebeClientLifecycle client;

	@PutMapping(value = "/start/{id}")
	public void start(@PathVariable("id") String id) {

		Map<String, Object> variables = new HashMap<>();
		variables.put("invoiceId", id);

		client.newCreateInstanceCommand().bpmnProcessId("Ex2").latestVersion().variables(variables).send().join();
	}
	
	
	

	@PutMapping(value = "/correlate/{id}")
	public void correlate(@PathVariable("id") String id) {

		client.newPublishMessageCommand().messageName("data_received").correlationKey(id).messageId("invoiceId").send()
				.join();
	}

}
