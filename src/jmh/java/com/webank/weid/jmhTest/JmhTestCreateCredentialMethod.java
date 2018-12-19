package com.webank.weid.jmhTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.webank.weid.base.JmhBase;
import com.webank.weid.protocol.request.CreateCredentialArgs;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(value = Scope.Benchmark)
public class JmhTestCreateCredentialMethod extends JmhBase {

    private List<CreateCredentialArgs> createCredentialArgsList = new ArrayList<>();
    private int index = 0;

    @Setup
    public void setup() {

    }

    @Benchmark
    public void createCredential() {
        int i = (int) (Math.random() * 100);
        credentialService.createCredential(createCredentialArgsList.get(i));
    }
}



