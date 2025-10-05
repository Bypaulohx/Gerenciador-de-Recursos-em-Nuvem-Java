package com.paulo.cloudmanager;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class EC2Service implements AutoCloseable {
    private final Ec2Client ec2;

    public EC2Service(String region) {
        this.ec2 = Ec2Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    public List<String> createInstances(String amiId,
                                        String instanceType,
                                        String keyName,
                                        List<String> securityGroupIds,
                                        int count,
                                        String nameTag,
                                        String userData) {
        String userDataBase64 = null;
        if (userData != null && !userData.isEmpty()) {
            userDataBase64 = Base64.getEncoder().encodeToString(userData.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }

        RunInstancesRequest.Builder runBuilder = RunInstancesRequest.builder()
                .imageId(amiId)
                .instanceType(InstanceType.fromValue(instanceType))
                .minCount(count)
                .maxCount(count);

        if (keyName != null && !keyName.isBlank()) runBuilder.keyName(keyName);
        if (securityGroupIds != null && !securityGroupIds.isEmpty()) runBuilder.securityGroupIds(securityGroupIds);
        if (userDataBase64 != null) runBuilder.userData(userDataBase64);

        RunInstancesResponse runResponse = ec2.runInstances(runBuilder.build());
        List<String> instanceIds = runResponse.instances().stream().map(Instance::instanceId).collect(Collectors.toList());

        if (nameTag != null && !nameTag.isBlank() && !instanceIds.isEmpty()) {
            CreateTagsRequest tagRequest = CreateTagsRequest.builder()
                    .resources(instanceIds)
                    .tags(Tag.builder().key("Name").value(nameTag).build())
                    .build();
            ec2.createTags(tagRequest);
        }

        return instanceIds;
    }

    public void describeInstances() {
        DescribeInstancesResponse response = ec2.describeInstances(DescribeInstancesRequest.builder().build());
        response.reservations().forEach(r -> r.instances().forEach(i -> {
            System.out.printf("ID: %s | Type: %s | State: %s | PublicIP: %s | AMI: %s%n",
                    i.instanceId(),
                    i.instanceTypeAsString(),
                    i.state() == null ? "UNKNOWN" : i.state().nameAsString(),
                    i.publicIpAddress() == null ? "-" : i.publicIpAddress(),
                    i.imageId());
        }));
    }

    public void startInstances(List<String> instanceIds) {
        ec2.startInstances(StartInstancesRequest.builder().instanceIds(instanceIds).build());
    }

    public void stopInstances(List<String> instanceIds) {
        ec2.stopInstances(StopInstancesRequest.builder().instanceIds(instanceIds).build());
    }

    public void terminateInstances(List<String> instanceIds) {
        ec2.terminateInstances(TerminateInstancesRequest.builder().instanceIds(instanceIds).build());
    }

    @Override
    public void close() {
        ec2.close();
    }
}