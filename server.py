import sys
import base64
from PIL import Image
from io import BytesIO
import socket
import threading
import Aes_hybrid
 
# Listen for TCP connections on port 65432 on any interface.
HOST = ''
PORT = 65480

  
def main():
  # Create a TCP server socket.
  s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
  s.bind((HOST, PORT))
  s.listen(1)
  
  while True:
    # Wait for a connection from a client.
    conn, addr = s.accept()
 
    # Handle the session in a separate thread.
    Session(conn).start();
 
# Session is a thread that handles a client connection.
class Session(threading.Thread):
  def __init__(self, conn):
    threading.Thread.__init__(self)
    self.conn = conn
 
  def run(self):
    roun=0
    width=0
    height=0
    #creating image frame
    dst = Image.new('RGB', (1000, 1000))
    while True:
      # Read a string from the client.
      line = self.conn.recv(25000)

      #decrypting image and decodeing and converting decoded bytes to image format
      image = Image.open(BytesIO(base64.b64decode(aes_2.decrypt('ahdfsujeytsbsdfawskdfhsdgfereijd',line))))
      
      #adding image segments to full image frame
      dst.paste(image,(width,height))
      if width<800:
        width+=200
      elif height <800 and width==800:
        height+=200
        width=0

      if line == '':
        # No more data from the client.  We're done.
        break
      else:
        #server sending the acknowledgement
        self.conn.send(b'5')
      line=0;
      roun+=1
      
    # showing the full image 
      if roun==25:
        dst.show()
    # conection closing.
    self.conn.close()
 
if __name__ == '__main__':
  main()
