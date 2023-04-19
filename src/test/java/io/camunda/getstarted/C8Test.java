package io.camunda.getstarted;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;

import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;

import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;

import static io.camunda.zeebe.process.test.assertions.BpmnAssert.assertThat;
import static io.camunda.zeebe.protocol.Protocol.USER_TASK_JOB_TYPE;
import static io.camunda.zeebe.spring.test.ZeebeTestThreadSupport.waitForProcessInstanceHasPassedElement;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ZeebeSpringTest
class C8Test {

	@Autowired
	private ZeebeClient zeebe;

	@Autowired
	private ZeebeTestEngine zeebeTestEngine;

	@Test
	public void testInvoice() throws Exception {
		// Prepare data input
		Map<String, Object> variables = new HashMap<>();
		variables.put("invoiceId", "89");
		variables.put("approved", true);

		// start a process instance
		ProcessInstanceEvent processInstance = zeebe.newCreateInstanceCommand() //
				.bpmnProcessId("Ex3").latestVersion() //
				.variables(variables) //
				.send().join();

		zeebeTestEngine.waitForIdleState(Duration.ofSeconds(10));

		zeebe.newPublishMessageCommand().messageName("data_received")
				.correlationKey((String) variables.get("invoiceId")).variables(variables).send();

		waitForUserTaskAndComplete("Activity_05m2wp9", Collections.singletonMap("approved", true));

		//Creating duration so that the send grid message gets sent
		//Not working as send grid email sending seems it only works in Camunda Cloud SaaS environment
		 Duration duration = Duration.ofMillis(50000);
		
	
	    //waitForProcessInstanceHasPassedElement(processInstance, "Activity_0zatqqh", duration);
		assertThat(processInstance).isNotCompleted();

	}

	public void waitForUserTaskAndComplete(String userTaskId, Map<String, Object> variables)
			throws InterruptedException, TimeoutException {
		// Let the workflow engine do whatever it needs to do
		zeebeTestEngine.waitForIdleState(Duration.ofSeconds(10));

		// Now get all user tasks
		List<ActivatedJob> jobs = zeebe.newActivateJobsCommand().jobType(USER_TASK_JOB_TYPE).maxJobsToActivate(1)
				.workerName("waitForUserTaskAndComplete").send().join().getJobs();

		// Should be only one
		assertTrue(jobs.size() > 0, "Job for user task '" + userTaskId + "' does not exist");
		ActivatedJob userTaskJob = jobs.get(0);
		// Make sure it is the right one
		if (userTaskId != null) {
			assertEquals(userTaskId, userTaskJob.getElementId());
		}

		// And complete it passing the variables
		if (variables != null && variables.size() > 0) {
			zeebe.newCompleteCommand(userTaskJob.getKey()).variables(variables).send().join();
		} else {
			zeebe.newCompleteCommand(userTaskJob.getKey()).send().join();
		}
	}

}