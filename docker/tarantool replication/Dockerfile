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

RUN cd mysql-tarantool-replication && git submodule update --init --recursive
RUN cd mysql-tarantool-replication && ls && cmake \. && make

