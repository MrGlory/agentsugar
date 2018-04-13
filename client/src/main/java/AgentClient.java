import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.util.List;

public class AgentClient {
    private final List<VirtualMachineDescriptor> listBefore;

    private final String jar;

    private final static String agentPath = "\\agent\\target\\agent-1.0-SNAPSHOT.jar";

    AgentClient(String attachJar, List<VirtualMachineDescriptor> vms) {
        listBefore = vms;  // 记录程序启动时的 VM 集合
        jar = attachJar;
    }

//    public void run() {
//        VirtualMachine vm = null;
//        List<VirtualMachineDescriptor> listAfter = null;
//        try {
//            int count = 0;
//            while (true) {
//                listAfter = VirtualMachine.list();
//                for (VirtualMachineDescriptor vmd : listAfter) {
//                    if (!listBefore.contains(vmd) && vmd.displayName().equals("org.ycz.agentwebdemo.AgentWebDemoApplication")) {
//                        // 如果 VM 有增加，我们就认为是被监控的 VM 启动了
//                        // 这时，我们开始监控这个 VM
//                        vm = VirtualMachine.attach(vmd);
//                        break;
//                    }
//                }
//                Thread.sleep(500);
//                count++;
//                if (null != vm || count >= 10) {
//                    break;
//                }
//            }
//            vm.loadAgent(jar);
////            vm.detach();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static void main(String[] args) throws Exception {
        //通过JVMTI API获取目前运行中的java程序，匹配需要添加代理服务的应用，与运行中的JVM虚拟机建立通讯，将agent应用加载到运行中运用中。
        String jarPath = System.getProperty("user.dir")+agentPath;
        for (VirtualMachineDescriptor vmd : VirtualMachine.list()) {
            if (vmd.displayName().contains("org.ycz.agentwebdemo.AgentWebDemoApplication")) {
                VirtualMachine vm = VirtualMachine.attach(vmd);
                vm.loadAgent(jarPath);
                vm.detach();
                break;
            }
        }
    }
}
