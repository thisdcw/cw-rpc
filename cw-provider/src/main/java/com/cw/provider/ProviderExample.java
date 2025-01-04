package com.cw.provider;

import com.cw.common.service.UserService;
import com.cw.core.bootstrap.ProviderBootstrap;
import com.cw.core.model.ServiceRegisterInfo;
import com.cw.provider.impl.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;

public class ProviderExample {

    public static void main(String[] args) {

        List<ServiceRegisterInfo<?>> serviceRegisterInfoList = new ArrayList<>();
        ServiceRegisterInfo<UserService> serviceRegisterInfo = new ServiceRegisterInfo<>(UserService.class.getName(), UserServiceImpl.class);
        serviceRegisterInfoList.add(serviceRegisterInfo);

        ProviderBootstrap.init(serviceRegisterInfoList);
    }
}
