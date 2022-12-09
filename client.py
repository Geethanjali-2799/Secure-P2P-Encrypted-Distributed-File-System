import sys
import Pyro4 as pyro
sys.excepthook = pyro.util.excepthook
# def user_choice():
#     choice = int(input("Enter your Choice:\n1.Create\n2.Read\n3.Write\n4.Delete\n0.Exit"))
#     while (choice != 0):
#         if (choice == 1):
#             filename = input("Enter File name:\n")
#             Server.create(filename)
#             choice = int(input("Enter your Choice:\n1.Create\n2.Read\n3.Write\n4.Delete\n0.Exit"))
#         elif (choice == 2):
#             filename = input("Enter File name:\n")
#             Server.read(filename)
#             choice = int(input("Enter your Choice:\n1.Create\n2.Read\n3.Write\n4.Delete\n0.Exit"))
#         elif (choice == 3):
#             filename = input("Enter File name:\n")
#             data = input("Enter the Data to be Added into File:\n")
#             Server.write(filename, data)
#             choice = int(input("Enter your Choice:\n1.Create\n2.Read\n3.Write\n4.Delete\n0.Exit"))
#         elif (choice == 4):
#             filename = input("Enter File name:")
#             Server.delete(filename)
#             choice = int(input("Enter your Choice:\n1.Create\n2.Read\n3.Write\n4.Delete\n0.Exit"))


def connect_to_peer(peer_ip,myip):
    objs = []
    with pyro.locateNS(host=peer_ip) as ns:
        for obj, obj_uri in ns.list(prefix="example.peer.server").items():
            print("found obj", obj)
            objs.append(pyro.Proxy(obj_uri))

    for obj in objs:
        res = obj.create("hello_world_test2.txt")
        print(res)

def main():
    myip= "10.200.151.152"
    objs = []
    with pyro.locateNS(host="10.200.5.26") as ns:
        for obj, obj_uri in ns.list(prefix="example.master.server").items():
            print("found obj", obj)
            objs.append(pyro.Proxy(obj_uri))

    for obj in objs:
        peer_server_ip  = obj.create("hello_world_test2.txt",myip)
        connect_to_peer(peer_server_ip,myip )

if __name__ == "_main_":
    main()