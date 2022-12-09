import sys
import Pyro4
import Pyro4.util
from server import Server

sys.excepthook = Pyro4.util.excepthook

server = Pyro4.Proxy("PYRONAME:example.server")
filename=input("Enter File name:")
Server.create(server,filename)