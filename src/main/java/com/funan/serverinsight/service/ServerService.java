package com.funan.serverinsight.service;

import com.funan.serverinsight.domain.*;
import com.funan.serverinsight.utils.MathUtils;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.Util;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;


public class ServerService {

    private final SystemInfo systemInfo;
    private final int waitMillis;

    public ServerService() {
        this(new SystemInfo(), 1_000);
    }

    public ServerService(SystemInfo systemInfo, int waitMillis) {
        this.systemInfo = Objects.requireNonNull(systemInfo);
        this.waitMillis = waitMillis;
    }

    public Server collect() {
        try {
            HardwareAbstractionLayer hal = systemInfo.getHardware();
            OperatingSystem os = systemInfo.getOperatingSystem();
            SysCpu cpu = collectCpu(hal.getProcessor());
            SysMem mem = collectMem(hal.getMemory());
            SysInfo sys = collectSysInfo();
            List<SysFile> files = collectFiles(os);
            return new Server(cpu, mem, sys, files);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to collect server info", e);
        }
    }

    private SysCpu collectCpu(CentralProcessor processor) {
        long[] prev = processor.getSystemCpuLoadTicks();
        Util.sleep(waitMillis);
        long[] curr = processor.getSystemCpuLoadTicks();

        long user = diff(curr, prev, CentralProcessor.TickType.USER);
        long nice = diff(curr, prev, CentralProcessor.TickType.NICE);
        long system = diff(curr, prev, CentralProcessor.TickType.SYSTEM);
        long idle = diff(curr, prev, CentralProcessor.TickType.IDLE);
        long ioWait = diff(curr, prev, CentralProcessor.TickType.IOWAIT);
        long irq = diff(curr, prev, CentralProcessor.TickType.IRQ);
        long softIrq = diff(curr, prev, CentralProcessor.TickType.SOFTIRQ);
        long steal = diff(curr, prev, CentralProcessor.TickType.STEAL);

        long total = user + nice + system + idle + ioWait + irq + softIrq + steal;

        SysCpu cpu = new SysCpu();
        cpu.setCpuNum(processor.getLogicalProcessorCount());
        cpu.setTotal(total);
        cpu.setUsed(user);
        cpu.setSys(system);
        cpu.setFree(idle);
        cpu.setWait(ioWait);
        return cpu;
    }

    private static long diff(long[] curr, long[] prev, CentralProcessor.TickType type) {
        return curr[type.getIndex()] - prev[type.getIndex()];
    }

    private SysMem collectMem(GlobalMemory memory) {
        SysMem mem = new SysMem();
        mem.setTotal(memory.getTotal());
        mem.setFree(memory.getAvailable());
        mem.setUsed(memory.getTotal() - memory.getAvailable());
        return mem;
    }

    private SysInfo collectSysInfo() {
        Properties p = System.getProperties();
        SysInfo sys = new SysInfo();
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            sys.setComputerName(localhost.getHostName());
            sys.setComputerIp(localhost.getHostAddress());
        } catch (Exception e) {
            sys.setComputerName("unknown");
            sys.setComputerIp("unknown");
        }
        sys.setOsName(p.getProperty("os.name"));
        sys.setOsArch(p.getProperty("os.arch"));
        sys.setUserDir(p.getProperty("user.dir"));
        return sys;
    }

    private List<SysFile> collectFiles(OperatingSystem os) {
        FileSystem fs = os.getFileSystem();
        List<OSFileStore> stores = fs.getFileStores();
        List<SysFile> list = new ArrayList<>(stores.size());

        for (OSFileStore store : stores) {
            long total = store.getTotalSpace();
            long free = store.getUsableSpace();
            long used = total - free;

            SysFile sf = new SysFile();
            sf.setDirName(store.getMount());
            sf.setSysTypeName(store.getType());
            sf.setTypeName(store.getName());
            sf.setTotal(MathUtils.convertFileSize(total));
            sf.setFree(MathUtils.convertFileSize(free));
            sf.setUsed(MathUtils.convertFileSize(used));
            sf.setUsage(MathUtils.mul(MathUtils.div(used, total, 2), 100));
            list.add(sf);
        }
        return list;
    }
}
