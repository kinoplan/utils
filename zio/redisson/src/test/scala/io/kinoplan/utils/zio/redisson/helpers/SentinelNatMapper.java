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

                String hostPort = null;
                if (mappedPort != null && mappedPort.length > 0) {
                    hostPort = mappedPort[0].getHostPortSpec();
                }

                if (uri.getPort() == port
                        && !uri.getHost().equals("redis")
                        && node.getNetworkAliases().contains("slave")) {
                    if (hostPort == null || hostPort.isEmpty()) {
                        continue;
                    }
                    try {
                        int parsedPort = Integer.parseInt(hostPort);
                        return new RedisURI(uri.getScheme(), "127.0.0.1", parsedPort);
                    } catch (NumberFormatException e) {
                        // Skip this node if host port is not a valid integer
                        continue;
                    }
                }

                if (mappedPort != null
                        && s.getIpAddress() != null
                        && s.getIpAddress().equals(uri.getHost())) {
                    if (hostPort == null || hostPort.isEmpty()) {
                        continue;
                    }
                    try {
                        int parsedPort = Integer.parseInt(hostPort);
                        return new RedisURI(uri.getScheme(), "127.0.0.1", parsedPort);
                    } catch (NumberFormatException e) {
                        // Skip this node if host port is not a valid integer
                        continue;
                    }
                }
            }
            return uri;
        };
    }
}