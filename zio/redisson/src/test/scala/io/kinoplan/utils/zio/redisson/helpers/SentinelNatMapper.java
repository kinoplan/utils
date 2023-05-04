package io.kinoplan.utils.zio.redisson.helpers;

import com.github.dockerjava.api.model.ContainerNetwork;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import org.redisson.api.NatMapper;
import org.redisson.misc.RedisURI;
import org.testcontainers.containers.GenericContainer;

import java.util.List;
import java.util.Map;

public class SentinelNatMapper {

    public static NatMapper make(int port, List<GenericContainer<?>> nodes) {
        return uri -> {
            for (GenericContainer<? extends GenericContainer<?>> node : nodes) {
                if (node.getContainerInfo() == null) {
                    continue;
                }

                Ports.Binding[] mappedPort = node.getContainerInfo().getNetworkSettings()
                        .getPorts().getBindings().get(new ExposedPort(uri.getPort()));

                Map<String, ContainerNetwork> ss = node.getContainerInfo().getNetworkSettings().getNetworks();
                ContainerNetwork s = ss.values().iterator().next();

                if (uri.getPort() == port
                        && !uri.getHost().equals("redis")
                        && node.getNetworkAliases().contains("slave")) {
                    return new RedisURI(uri.getScheme(), "127.0.0.1", Integer.parseInt(mappedPort[0].getHostPortSpec()));
                }

                if (mappedPort != null
                        && s.getIpAddress() != null
                        && s.getIpAddress().equals(uri.getHost())) {
                    return new RedisURI(uri.getScheme(), "127.0.0.1", Integer.parseInt(mappedPort[0].getHostPortSpec()));
                }
            }
            return uri;
        };
    }
}