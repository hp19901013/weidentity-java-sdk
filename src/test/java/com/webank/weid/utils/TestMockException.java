package com.webank.weid.utils;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import mockit.Mock;
import mockit.MockUp;
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.abi.datatypes.DynamicBytes;
import org.bcos.web3j.abi.datatypes.generated.Bytes32;
import org.bcos.web3j.abi.datatypes.generated.Int256;

import com.webank.weid.contract.WeIdContract;


public class TestMockException {

    /**
     * mockTimeoutFuture.
     */
    public static MockUp<Future<?>> mockTimeoutFuture() {
        return new MockUp<Future<?>>() {
            @Mock
            public Future<?> get(long timeout, TimeUnit unit)
                throws TimeoutException {

                throw new TimeoutException();
            }
        };
    }

    /**
     * mockInterruptedFuture.
     */
    public static MockUp<Future<?>> mockInterruptedFuture() {
        return new MockUp<Future<?>>() {
            @Mock
            public Future<?> get(long timeout, TimeUnit unit)
                throws InterruptedException {

                throw new InterruptedException();
            }

            @Mock
            public Future<?> get()
                throws InterruptedException {

                throw new InterruptedException();
            }
        };
    }

    /**
     * mockReturnNullFuture.
     */
    public static MockUp<Future<?>> mockReturnNullFuture() {
        return new MockUp<Future<?>>() {
            @Mock
            public Future<?> get(long timeout, TimeUnit unit) {
                return null;
            }
        };
    }

    /**
     * mockSetAttribute.
     */
    public static MockUp<WeIdContract> mockSetAttribute(MockUp<Future<?>> mockFuture) {
        return new MockUp<WeIdContract>() {
            @Mock
            public Future<?> setAttribute(
                Address identity,
                Bytes32 key,
                DynamicBytes value,
                Int256 updated) {
                return mockFuture.getMockInstance();
            }
        };
    }
}
