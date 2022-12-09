from __future__ import print_function
import Pyro4 as pyro
import os
# @Pyro4.behavior(instance_mode="single")

@pyro.expose
class Server(object):
    def create(self, filename):
        dir_path = "./Files/"
        filename = filename + '.txt'
        fp = open(os.path.join(dir_path, filename), 'x')
        fp.close()
        return "tarun"

def register_master(master_IP, HOST_IP):
    try:
        objs = []
        with pyro.locateNS(host=master_IP) as ns:
            for obj, obj_uri in ns.list(prefix="example.master.server").items():
                print("found obj", obj)
                objs.append(pyro.Proxy(obj_uri))

        for obj in objs:
            obj.register_user(HOST_IP)

        return True
    except Exception as e:
        print("Couldn't register")
        raise

def main():
    master_IP = "10.200.5.26"
    HOST_IP = "10.200.5.26"
    HOST_PORT = 9093
    if register_master(master_IP, HOST_IP):
        print("Successfully registered with the master")
    server_obj = Server()
    with pyro.Daemon(host=HOST_IP, port=HOST_PORT) as daemon:
        obj_uri=daemon.register(server_obj)
        with pyro.locateNS() as ns:
            ns.register("example.peer.server", obj_uri)
        print("Server available")
        daemon.requestLoop()


if __name__ == "__main__":
    main()


