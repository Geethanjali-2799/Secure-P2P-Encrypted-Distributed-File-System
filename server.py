from __future__ import print_function

from pathlib import Path

import Pyro4 as pyro
import os



MASTER_IP = "10.200.5.26"
HOST_IP = "10.200.5.26"
HOST_PORT = 9093


@pyro.expose
class Server(object):
    def __init__(self):
        self.message = ""
        self.dir_path = './Files/'

    def create(self, name):
        my_file = Path(self.dir_path + name)
        if my_file.is_file():
            self.message = "File Already Exists"
        else:
            fp = open(self.dir_path + name, 'x')
            fp.close()
            self.message = "File Created Successfully"
        print(self.message)
        return self.message

    def read(self, name):
        with open(self.dir_path + name) as f:
            return f.read()
        return "file doesn't exist"

    def write(self, filename, data):
        with open(self.dir_path + filename, 'w') as f:
            f.write(data)


def register_master(master_IP, HOST_IP):
    try:
        objs = []
        with pyro.locateNS(host=master_IP) as ns:
            for obj, obj_uri in ns.list(prefix="master.server").items():
                print("found obj", obj)
                objs.append(pyro.Proxy(obj_uri))

        for obj in objs:
            obj.register_user(HOST_IP)
        return True
    except Exception as e:
        print("Couldn't register")
        raise


def main():
    if register_master(MASTER_IP, HOST_IP):
        print("Successfully registered with the master")
    server_obj = Server()
    with pyro.Daemon(host=HOST_IP, port=HOST_PORT) as daemon:
        obj_uri = daemon.register(server_obj)
        with pyro.locateNS() as ns:
            ns.register("peer.server", obj_uri)
        print("Server available")
        daemon.requestLoop()


if __name__ == "__main__":
    main()
