package org.joe_e.servlet.test;

import java.lang.management.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class JMXMonitor {

	public static void main(String[] args) throws Exception {
		JMXServiceURL u = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:8999/jmxrmi");
		JMXConnector c = JMXConnectorFactory.connect(u);
		MBeanServerConnection msbc = c.getMBeanServerConnection();
		
		MemoryMXBean mbean = ManagementFactory.newPlatformMXBeanProxy(msbc, ManagementFactory.MEMORY_MXBEAN_NAME, MemoryMXBean.class);
		ThreadMXBean tbean = ManagementFactory.newPlatformMXBeanProxy(msbc, ManagementFactory.THREAD_MXBEAN_NAME, ThreadMXBean.class);
		ClassLoadingMXBean cbean = ManagementFactory.newPlatformMXBeanProxy(msbc, ManagementFactory.CLASS_LOADING_MXBEAN_NAME, ClassLoadingMXBean.class);

		mbean.gc();
		while (true) {
		    System.out.println(mbean.getHeapMemoryUsage().getUsed() + " " + mbean.getObjectPendingFinalizationCount() + " "  + tbean.getThreadCount() + " " + cbean.getLoadedClassCount());
			Thread.sleep(1000);
		}
	}
}
