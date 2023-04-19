package io.camunda.getstarted;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;

@Component
@EnableZeebeClient
public class RetrieveData {

	@ZeebeWorker(type = "retrieveData", autoComplete = true)
	public void calculateScore(final JobClient client, final ActivatedJob job) {

		Map<String, Object> variables = new HashMap<>();

		variables = job.getVariablesAsMap();
		String invoiceID = (String) variables.get("invoiceId");

		System.out.println("invoice id lenght  " + invoiceID.length());

		if (invoiceID.length() > 2) {
			client.newThrowErrorCommand(job).errorCode("invalidID").send();
			

		} else {
			client.newCompleteCommand(job.getKey()).send().join();

		}

	}
	

	/*
	 * //This extra worker is being used to complete the happy path of the Junit
	 * test
	 * 
	 * @ZeebeWorker(type = "sendApproval", autoComplete = true) public void
	 * sendMessage(final JobClient client, final ActivatedJob job) {
	 * 
	 * client.newCompleteCommand(job.getKey()).send().join();
	 * 
	 * 
	 * 
	 * }
	 */
	
}
