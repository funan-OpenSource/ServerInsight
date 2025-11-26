package com.funan.serverinsight.domain;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 服务器相关信息
 *
 * @author funan
 */
public final class Server {

    private final SysCpu cpu;
    private final SysMem mem;
    private final SysInfo sys;
    private final List<SysFile> sysFiles;

    /* 包权限构造器*/
    public Server(SysCpu cpu,
                  SysMem mem,
                  SysInfo sys,
                  List<SysFile> sysFiles) {
        this.cpu = Objects.requireNonNull(cpu);
        this.mem = Objects.requireNonNull(mem);
        this.sys = Objects.requireNonNull(sys);
        this.sysFiles = Collections.unmodifiableList(new ArrayList<>(sysFiles));
    }

    public SysCpu getCpu() {
        return cpu;
    }

    public SysMem getMem() {
        return mem;
    }

    public SysInfo getSys() {
        return sys;
    }

    public List<SysFile> getSysFiles() {
        return sysFiles;
    }
}
