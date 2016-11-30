/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import static app.Client.F;
import static app.Client.N;
import static app.Client.lista;
import static app.Client.no;
import static app.Client.total;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

class DownloadHandler extends Thread {
    private ObjectInputStream in1;
    private ObjectOutputStream out1;
    private String flag = "true";

    public DownloadHandler() {}

    public void run() {
        try{

            Socket req = null;
            while (true){
                    try {
                            req = new Socket("localhost", N);
                            if (req != null) { break; }
                    }catch (IOException e) { Thread.sleep(1000); }
            }

            System.out.println("conectado com outro cliente");

            out1 = new ObjectOutputStream(req.getOutputStream());
            out1.flush();
            in1 = new ObjectInputStream(req.getInputStream());

            int noChunks = Integer.parseInt((String)in1.readObject());
            ArrayList<Integer> DNlist = new ArrayList<Integer>();
            for(int j = 0; j < noChunks; j++){
                    DNlist.add(Integer.parseInt((String)in1.readObject()));
            }

            System.out.println("DownloadClientelist"+DNlist);
            System.out.println("minhalist"+lista);

            ArrayList<Integer> Requerlist = compare(DNlist,lista);
            System.out.println("Requerlist sent to UploadNeighbour"+Requerlist);

            enviarMenssagem(""+Requerlist.size());
            enviarLista(Requerlist);


            for(int k=0;k<Requerlist.size();k++){
                    File f1 = (File)in1.readObject();
                    int I = Requerlist.get(k);
                    System.out.println("list"+lista);

                    File f = new File("C:\\Users\\rish\\Desktop\\CN\\Client"+no+"\\Chunk"+I);
                    System.out.println("Chunk"+I+"Download recebido de outro cliente");

                    InputStream input = null;
                    OutputStream output = null;
                    try {
                            input = new FileInputStream(f1);
                            output = new FileOutputStream(f);
                            byte[] buf = new byte[102400];
                            int bytesRead;
                            while ((bytesRead = input.read(buf)) > 0) {
                                    output.write(buf, 0, bytesRead);
                            }
                    } finally {
                            input.close();
                            output.close();
                    }
                    F.add(f);
                    lista.add(I);
            }

            if(lista.size() == total){
                    flag = "false";
            }
            enviarMenssagem(flag);

            System.out.println("lista depois de receber "+lista);
    }
    catch(UnknownHostException e1){}
    catch(IOException e2){}
    catch(ClassNotFoundException e3){}
    catch(InterruptedException e4){}
    }


    void enviarLista(ArrayList<Integer> lista1){
            for(int j = 0; j < lista1.size(); j++){
                    enviarMenssagem(""+lista1.get(j));
            }
    }

    ArrayList<Integer> compare(ArrayList<Integer> lista1, ArrayList<Integer> lista2){
            ArrayList<Integer> l = new ArrayList<Integer>();
            int flag = 0;
            for(int i = 0; i < lista1.size(); i++){
                    for(int j = 0; j < lista2.size(); j++){
                            if(lista1.get(i) == lista2.get(j)){
                                    flag = 1;
                            }	
                    }
                    if(flag == 0){
                            l.add(lista1.get(i));
                    }
                    flag = 0;
            }		
            return l;
    }

    void enviarMenssagem(String msg){
            try{
                    out1.writeObject(msg);
                    out1.flush();
            }catch(IOException ioException){}
    }
}
