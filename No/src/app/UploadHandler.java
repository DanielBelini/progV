
package app;

import static app.Client.F;
import static app.Client.UNflag;
import static app.Client.lista;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;


class UploadHandler extends Thread {
private Socket conecxao;
private ObjectInputStream in1;
private ObjectOutputStream out1;

public UploadHandler(Socket conecxao) {
        this.conecxao = conecxao;
}

public void run() {
            try{
                    System.out.println("Upload dentro do manipulador.");
                    out1 = new ObjectOutputStream(conecxao.getOutputStream());
                    out1.flush();
                    in1 = new ObjectInputStream(conecxao.getInputStream());

                    enviarMensagem(""+lista.size());
                    enviarLista(lista); 

                    int noChunks = Integer.parseInt((String)in1.readObject());
                    ArrayList<Integer> Requerlist = new ArrayList<Integer>();
                    for(int j = 0; j < noChunks; j++){
                            Requerlist.add(Integer.parseInt((String)in1.readObject()));
                    }
                    System.out.println("Requerlist do upload de cliente  "+Requerlist);


                    for(int j = 0; j < Requerlist.size(); j++){
                            int index = lista.indexOf(Requerlist.get(j));
                            int I = Requerlist.get(j);
                            enviarArquivo(F.get(index));
                            System.out.println("Chunk"+I+"enviando o Upload para o proximo cliente");
                    }

                    String j1 = (String)in1.readObject();
                    if(j1.equals("false"))
                    UNflag = false;
                    System.out.println("Flag"+UNflag);

                    Thread.sleep(2000);
            }
            catch(UnknownHostException e1){}
            catch(IOException e2){}
            catch(ClassNotFoundException e3){}
            catch(InterruptedException e4){}
    }


    public void enviarArquivo(File f){
            try{
                    out1.writeObject(f);
                    out1.flush();
            }catch(IOException ioException){}
    }


    void enviarLista(ArrayList<Integer> l1){
            for(int j = 0; j < l1.size(); j++){
                    enviarMensagem(""+l1.get(j));
            }
    }

    void enviarMensagem(String msg){
            try{
                    out1.writeObject(msg);
                    out1.flush();
            }catch(IOException ioException){}
    }
}

