FROM centos:7.9.2009

#RUN cd /etc/yum.repos.d/ && \
#    sed -i 's/mirrorlist/#mirrorlist/g' /etc/yum.repos.d/CentOS-* &&\
#    sed -i 's|#baseurl=http://mirror.centos.org|baseurl=http://vault.centos.org|g' /etc/yum.repos.d/CentOS-*

RUN yum -y install epel-release && \
    yum -y update && \
    yum -y clean all
#RUN yum  module disable mysql &&  \
#RUN #yum -y --enablerepo=powertools install mysql-community-server
RUN yum -y install mysql
RUN yum -y install  git ncurses-devel cmake gcc-c++ boost boost-devel wget unzip nano bzip2 mysql-devel mysql-lib make

RUN git clone https://github.com/tarantool/mysql-tarantool-replication.git

#RUN git --version && cd mysql-tarantool-replication &&

WORKDIR mysql-tarantool-replication

COPY lib lib/

RUN ls lib/*
RUN cmake \.
RUN make

COPY replicatord /usr/local/sbin/replicatord
COPY replicatord.service /etc/systemd/system
COPY replicatord.yml /usr/local/etc/replicatord.yml

ENV container docker
RUN (cd /lib/systemd/system/sysinit.target.wants/; for i in *; do [ $i == \
systemd-tmpfiles-setup.service ] || rm -f $i; done); \
rm -f /lib/systemd/system/multi-user.target.wants/*;\
rm -f /etc/systemd/system/*.wants/*;\
rm -f /lib/systemd/system/local-fs.target.wants/*; \
rm -f /lib/systemd/system/sockets.target.wants/*udev*; \
rm -f /lib/systemd/system/sockets.target.wants/*initctl*; \
rm -f /lib/systemd/system/basic.target.wants/*;\
rm -f /lib/systemd/system/anaconda.target.wants/*;
VOLUME [ "/sys/fs/cgroup" ]
CMD ["/usr/sbin/init"]

CMD ["systemctl","start","replicatord"]

