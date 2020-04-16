import sys
import base64
from PIL import Image
from io import BytesIO
import socket
import threading
import aes_2
 
# Listen for TCP connections on port 65432 on any interface.
HOST = ''
PORT = 65495
  
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
    print("sdsdsd")
    roun=0
    width=0
    height=0
    P=230
    G=99
    #creating generated key of the server
    gen_key=(G^35)%P
    gen_key_bytes=str(gen_key)+'\r\n'
    sec_key=0
    #creating image frame
    dst = Image.new('RGB', (1000, 1000))
    while True:
      # Read a generated key from the client.
      line = self.conn.recv(25000)
      if line == '':
        # No more data from the client.  We're done.
        break
      elif roun==0:
        #Generating the secret key
        sec_key=(int(line)^35)%P
        #sending server generated key to client
        self.conn.sendall(gen_key_bytes.encode('utf-8'))
      else:
        #decrypting image and decodeing and converting decoded bytes to image format
        image = Image.open(BytesIO(base64.b64decode(aes_2.decrypt(str(sec_key)+'gdbtiomagRDbsHLkdfhsdgfereijd',line))))
        #adding image segments to full image frame
        dst.paste(image,(width,height))
        if width<800:
          width+=200
        elif height <800 and width==800:
          height+=200
          width=0
        self.conn.send(b'5')
      line=0
      roun+=1
      
    # showing the full image 
      if roun==26:
        dst.show()
    # conection closing.
    self.conn.close()
 
if __name__ == '__main__':
  main()
