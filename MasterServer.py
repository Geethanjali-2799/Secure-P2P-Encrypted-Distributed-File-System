from __future__ import print_function

import collections
import random

import Pyro4
import os

@Pyro4.expose
@Pyro4.behavior(instance_mode="single")
class MasterServer(object):
    def __init__(self):
        self.registered_users = set()
        self.file_data = collections.defaultdict(set)
        self.read_permissions = collections.defaultdict(set)
        self.write_permissions = collections.defaultdict(set)
        self.delete_permissions = collections.defaultdict(set)
        self.file_deleted = {}

    def register_user(self, user_ip):
        self.registered_users.add(user_ip)
        return True

    def create(self, file_name, user_id):
        user = random.choice(list(self.registered_users))
        self.read_permissions[file_name].add(user_id)
        self.write_permissions[file_name].add(user_id)
        self.delete_permissions[file_name].add(user_id)
        self.file_deleted[file_name] = False
        return user

    def read(self, file_name):
        pass



def main():
    server_obj = MasterServer()
    masterIP = "10.200.5.26"
    master_port = 9096
    with Pyro4.Daemon(host=masterIP, port=master_port) as daemon:
        obj_uri = daemon.register(server_obj)
        with Pyro4.locateNS() as ns:
            ns.register("example.master.server", obj_uri)
        print("Server available")
        daemon.requestLoop()

if __name__=="__main__":
    main()








