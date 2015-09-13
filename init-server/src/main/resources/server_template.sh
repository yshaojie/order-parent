#!/bin/bash

# template properites:
# server_name: the instance name of the server process,should be unique in all machines.
# server_class_path: the class path of the server process,may be empty
# server_main_class: the main class of the server process,must be set
# server_args: the arguments of of the server process,may be empty

###########################################
########replaced parameters################
###########################################
server_args="{{server_args}}"
server_jvm_args="{{server_jvm_args}}"
server_name="{{server_name}}"
server_main_class="{{server_main_class}}"
server_home="{{server_home}}"
server_log_home="{{server_name}}"
server_resources="{{server_home}}/resources/"
###########################################

get_server_pids() {
    pids=( `ps -eo pid,user,cmd | grep "${server_home}" | grep "${server_main_class}" | awk '{print $1}'` )
}

start_server() {
    echo "starting server ${server_name}"
    #all server pids
    get_server_pids
    if [ ${#pids[*]} -gt 0 ]; then
        echo "${server_name} is started,the pid is ${pids[@]}"
        exit 1
    fi

    #find logback-classic jar
    logbacks=( `find lib/ -name logback-classic* | sort -fr` )
    if [ "${#logbacks[@]}" -gt 0 ]; then
        #exist logback-classic jar
        echo "find ${logbacks[0]}, set first to the java classpath"
        server_class_path=${server_home}/${logbacks[0]}
    fi

    #iterator server jar libs
    for i in `ls ${server_home}/lib/ | sort -rf`
    do
        server_class_path=$server_class_path:${server_home}/lib/$i
    done
    export CLASSPATH=${server_class_path}
    #server start time
    start_time=`date "+%Y-%m-%d %H:%M:%S"`

    #start the server
    java ${server_jvm_args} -Dstart_time="${start_time}" -Dserver_home=${server_home} -Dserver_log_home=${server_log_home} -Dserver_resources=${server_resources} -Dserver_name=${server_name} ${system_props} ${server_main_class} ${server_args} >> ${server_log_home}/${server_name}.log 2>&1 &
    sleep 3
    get_server_pids
    if [ ${#pids[*]} -gt 1 ]; then
        echo "${server_name} is start success,but find multiple instances pids: ${pids[@]}"
        exit 1
    elif [ ${#pids[*]} -eq 1 ]; then
        echo "${server_name} is start success,the pid is ${pids[@]}"
    else
        echo "Fail,see log ${server_log_home}/${server_name}.log"
        exit 1
    fi
}

stop_server() {
    echo "stoping server ${server_name}"
    #all pids
    get_server_pids
    if [ ${#pids[*]} -gt 1 ]; then #multiple server
        echo "find multiple ${server_name} proccess, infos:"
        ps -eo pid,user,cmd | grep "${server_resources}" | grep "${server_main_class}"
        echo "stop server fail,please manual processing"
        exit 1
    elif [ ${#pids[*]} -eq 1 ]; then
        echo "kill ${pids[@]}"
        kill ${pids[@]}
        for (( i = 0; i < 5; i ++ ))
        do
            sleep 1
            get_server_pids
            if [ ${#pids[*]} -le 0 ]; then
                break
            fi
        done
        if [ ${#pids[*]} -gt 0 ]; then
            echo "stop server fail,please manual processing"
            exit 1
        else
            echo "stop server success"
        fi
    else
        echo "server ${server_name} is not exist"
    fi
}

case "$1" in
    start)
        start_server
    ;;
    stop)
        stop_server
    ;;
    restart)
        stop_server
        start_server
    ;;
    *)
        echo "Usage ${0} <start|stop|restart>"
        exit 1
    ;;
esac
exit 0
