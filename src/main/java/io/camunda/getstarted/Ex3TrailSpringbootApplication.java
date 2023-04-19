package io.camunda.getstarted;


import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeDeployment;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
@EnableZeebeClient
@ZeebeDeployment(resources = {"static/bpmn/ex3.bpmn"})
public class Ex3TrailSpringbootApplication {

  public static void main(String[] args) {
    SpringApplication.run(Ex3TrailSpringbootApplication.class, args);
  }

}
