from __future__ import print_function
import Pyro4
import os

@Pyro4.expose
@Pyro4.behavior(instance_mode="single")

class Server(object):

    def create(self, filename):
        dir_path = "./Files/"
        filename=filename+'.txt'
        fp=open(os.path.join(dir_path, filename),'x')
        fp.close()


def main():
    Pyro4.Daemon.serveSimple(
            {
                Server: "example.server"
            },
            ns = True)

if __name__=="__main__":
    main()








