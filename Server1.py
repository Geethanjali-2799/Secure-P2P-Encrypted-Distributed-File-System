#!/usr/bin/env python
# coding: utf-8

# In[1]:


import os
import sys
import pickle
import json
import socket as s
import threading
import traceback
from socketserver import ThreadingTCPServer


# In[2]:


#IP_address = s.gethostbyname(s.gethostname())
IP_address = '127.0.0.1'
port = 3333
buffer = 1024
max_connections = 1000
encoding_format = 'utf-8'
print(IP_address)


# In[3]:


#IP_address = sys.argv[1]
#port = sys.argv[2]

def perform_operation(conn, addr):
    print(f'[Connected] Connection Established with {addr}')
#     conn.send("ACK@Welcome to File Server\n".encode(encoding_format))
    
    lock = threading.Lock()
    
    while True:
        #data1 = conn.recv(buffer).decode(encoding_format)
        #print(data1)
        data1 = conn.recv(buffer)
        data = pickle.loads(data1)
        #data = conn.recv(buffer).decode(encoding_format)
        #data = data.split("@")
        print(data)
        operation = data[0]
        print(operation)
        
        if operation.lower().strip() == "logout":
            threading.activeCount() +1
            break
        ###################################################################################################    
        # This section is for getting the help to know which functions you can implement on the dfs system 
        ###################################################################################################
        if operation.lower().strip() == "help":
            data = "ACK@"
            data += "createdir: To create directory in server\n" 
            data += "createfile: To create file in server\n" 
            data += "deletefile: To delete file from server\n" 
            data += "writefile: To write the file present in the server\n" 
            data += "renamefile: To rename the file in the server\n"
            data += "listfile: To get the list of files present in the server\n" 
            data += "readfile: To read the content of the file present in teh server\n" 
            data += "Help: Lists all supported operations\n"
            data += "Logout: Disconnect from the server\n"
            
            conn.send(data.encode())
            
        #############################################################
        # This section is for creating the directory into the server
        #############################################################
        if operation.lower().strip() == "createdir":
            #print(data[2])
            lock.acquire()
            dirname = data[2]
#             user = data[1]
#             if os.path.exists('files/'+user) == False:
#                 os.mkdir('files/'+user)
            if os.path.exists('files/'+dirname) == False:
                os.mkdir('files/'+dirname)
            conn.send("ACK@Directory Created".encode(encoding_format))
            print("Directory Created: "+dirname)
            lock.release()
            
            
        ############################################################    
        # This section is for creating the new file into the server 
        ############################################################
        if operation.lower().strip() == "createfile":
            lock.acquire()
#             user = data[1]
            dirname = data[2]
            filename = data[3]
            if os.path.exists('files/'+dirname):
                open('files/'+dirname+"/"+filename, 'w').close()
                conn.send("ACK@File Created".encode())
                print("File Created: "+filename)
            else:
                conn.send("ACK@Given path does not exists".encode())
                print("Given path does not exists: "+filename)
            lock.release()
                
                
             
        #########################################################    
        # This section is for deleting the file from the server
        ########################################################
        if operation.lower().strip() == "deletefile":
            lock.acquire()
#             user = data[1]
            dirname = data[2]
            if os.path.exists('files/'+dirname):
                if os.path.isdir('files/'+dirname):
                    shutil.rmtree('files/'+dirname)
                if os.path.isfile('files/'+dirname):    
                    os.remove('files/'+dirname)
                conn.send("ACK@Given file deleted".encode())
                print("Given file deleted: "+dirname)
            else:
                conn.send("ACK@file does not exists".encode())
                print("file does not exists: "+dirname)
            lock.release()
        
        
        
        #########################################################
        #This section is for writing the content in the file
        #########################################################
        if operation.lower().strip() == "writefile":
            lock.acquire()
#             user = data[1]
            dirname = data[2]
            filename = data[3]
            encrypt = data[4]
            if os.path.exists('files/'+dirname+"/"+filename):
                f = open('files/'+dirname+"/"+filename, "a")
                f.write(encrypt)
                f.close()
                conn.send("ACK@file data saved at server".encode())
                print("file data saved at server: "+filename)
            else:
                conn.send("ACK@file does not exists".encode())                
                print("file does nots exists: "+filename)  
            lock.release()
                    
                    
          
        
        ##########################################################
        #This section is for renaming the file with provided name
        ##########################################################
        if operation.lower().strip() == "renamefile":
            lock.acquire()
#             user = data[1]
            dirname = data[2]
            oldname = data[3]
            newname = data[4]
            if os.path.exists('files/'+dirname+"/"+oldname):
                os.rename('files/'+dirname+"/"+oldname,'files/'+dirname+"/"+newname)
                conn.send("ACK@file rename at server".encode())
                print("file rename at server: "+newname)
            else:
                conn.send("ACK@file does not exists".encode())
                print("file does not exists")
            lock.release()
                    
                    
        #############################################################
        #This section will show the list of files in given directory
        #############################################################
        if operation.lower().strip() == "listfiles":
#             user = data[1]
            file_list = []
    
            path = 'C:/Users/dhruv/Desktop/PCS Final Project/Distributed_File_System/server1/files'
            for root, directories, files in os.walk(path, topdown=False):
                for name in files:
                    file_list.append(name)
                for name in directories:
                    file_list.append(name)
            print(file_list)
            file_list.pop()
            file_list = pickle.dumps(file_list)
            conn.send(file_list)
            print("file list sent to user")        
                        
                
        ################################################################
        #This section will read and display the content of a given file
        ################################################################
        if operation.lower().strip() == "readfile":
#             user = data[1]
            dirname = data[2]
            filename = data[3]
            features = {}
            if os.path.exists('files/'+dirname+"/"+filename):
                with open('files/'+dirname+"/"+filename) as f:
                    dataset = f.read()
                f.close()
                features['status'] = "correct"
                features['data']= dataset
                print(features)
                read_data = json.dumps(features)
#                 read_data = pickle.dumps(features)
                print(read_data)
                conn.send(read_data.encode())
                print("file sent to server: "+filename)
            else:
                features.append("incorrect")
                features = pickle.dumps(features)
                conn.send(features.encode())
                print("file does not exists")            
            
         
    #conn.sendall(f"Hello from server\n".encode('utf-8'))
    print(f"[Disconnect] Disconnected {addr} client")
    conn.close()


# In[4]:


def create_server():
    #creating a new socket
    #socket functions takes as argument the socket family and socket type
    with s.socket(s.AF_INET, s.SOCK_STREAM) as server:
        server.setsockopt(s.SOL_SOCKET, s.SO_REUSEADDR, 1)
        try:
            server.bind((IP_address, port))
        except:
            print("Error in binding the socket")
            sys.exit()
        thread_list = []
        server.listen(max_connections) #queues upto 10 requests
        print(f"Server listening upto {max_connections - (threading.activeCount()-1)} connections\n")
        while True:
            connection, client_addr = server.accept()
            try:
                conn_thread = threading.Thread(target=perform_operation, args=(connection, client_addr))
                conn_thread.start()
                #thread_list.append(conn_thread)
                print(f"Total Active connections {threading.activeCount()-1}")
            except:
                print("Error in starting the thread")
                traceback.print_exc()
        '''if len(thread_list) >0:
            for t in thread_list:
                 t.join()'''


# In[ ]:


if __name__ == "__main__":
    create_server()


# In[ ]:




