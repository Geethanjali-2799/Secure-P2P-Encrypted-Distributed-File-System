from __future__ import print_function

import collections
import random

import Pyro4
import os
import pandas as pd

MASTER_IP = "10.200.5.26"
MASTER_PORT = 9096


@Pyro4.expose
@Pyro4.behavior(instance_mode="single")
class MasterServer(object):
    def __init__(self):
        self.replica = 3
        self.registered_users = set()
        self.file_data = collections.defaultdict(set)
        self.read_permissions = collections.defaultdict(set)
        self.write_permissions = collections.defaultdict(set)
        self.delete_permissions = collections.defaultdict(set)
        self.file_deleted = {}

        # update the all users
        self.all_users = {}
        validation = pd.DataFrame(pd.read_excel("validation.xlsx"))
        for i in range(len(validation)):
            self.all_users[validation.loc[i][0]] = validation.loc[i][1]

    def register_user(self, user_ip):
        self.registered_users.add(user_ip)
        return True

    def random_user_ips(self):
        if len(self.registered_users) <= self.replica:
            return self.registered_users
        users = []
        reg_users = list(self.registered_users)
        idx = 0
        while idx < self.replica:
            rand = random.choice(reg_users)
            reg_users.remove(rand)
            users.append(rand)
            idx += 1
        return users

    def create(self, file_name, user_ip):
        users = self.random_user_ips()
        for user in users:
            self.file_data[file_name].add(user)
            self.read_permissions[file_name].add(user_ip)
            self.write_permissions[file_name].add(user_ip)
            self.delete_permissions[file_name].add(user_ip)
        self.file_deleted[file_name] = False
        return user

    def read(self, name, user_ip):
        if (name in self.file_deleted and self.file_deleted[name]) or \
                name not in self.file_deleted:
            return "file doesn't exist"
        if user_ip not in self.read_permissions[name]:
            return "you do not have read permission"
        users = list(self.file_data[name])
        return users[0]

    def write(self, name, user_ip):
        if (name in self.file_deleted and self.file_deleted[name]) or \
                name not in self.file_deleted:
            return "file doesn't exist"
        if user_ip not in self.write_permissions[name]:
            return "you do not have write permission"
        users = list(self.file_data[name])
        return users

    def delete(self, name, user_ip):
        if (name in self.file_deleted and self.file_deleted[name]) or \
                name not in self.file_deleted:
            return "file doesn't exist"
        if user_ip not in self.delete_permissions[name]:
            return "you do not have delete/restore permission"

        self.file_deleted[name] = True

    def restore(self, name, user_ip):
        if (name in self.file_deleted and not self.file_deleted[name]) or \
                name not in self.file_deleted:
            return "file doesn't exist"
        if user_ip not in self.delete_permissions[name]:
            return "you do not have delete/restore permission"

        self.file_deleted[name] = True


def main():
    server_obj = MasterServer()
    with Pyro4.Daemon(host=MASTER_IP, port=MASTER_PORT) as daemon:
        obj_uri = daemon.register(server_obj)
        with Pyro4.locateNS() as ns:
            ns.register("master.server", obj_uri)
        print("Master Server now available")
        daemon.requestLoop()


if __name__ == "__main__":
    main()
