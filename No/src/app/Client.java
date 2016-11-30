package app;

import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Client {
	Socket socketNo;          
	ObjectOutputStream out;       
 	ObjectInputStream in;      
	
	public static ArrayList<File> F = new ArrayList<File>();
	public static ArrayList<Integer> lista = new ArrayList<Integer>();
	
	private static int clientePort;	
	public static Boolean UNflag = true; 
	public static int N; 
	public static String sc;
	public static int limit;
	public static int total;
	public static int no; 
        
	public void Client() {}
	

	void run(){
		try{
			socketNo = new Socket("localhost", 8000);
			System.out.println("conectado no localhost na porta 8000");
			
			out = new ObjectOutputStream(socketNo.getOutputStream());
			out.flush();
			in = new ObjectInputStream(socketNo.getInputStream());
			
			no = Integer.parseInt((String)in.readObject());
			sc = (String)in.readObject();
			total = Integer.parseInt((String)in.readObject());
			limit = Integer.parseInt((String)in.readObject());
			
			for(int i=0;i<limit;i++){
				File f1 = (File)in.readObject();
				int I = Integer.parseInt((String)in.readObject());

				File arquivo = new File("C:\\Users\\rish\\Desktop\\CN\\Client"+no+"\\Chunk"+I);
				System.out.println(" Chunk recebido "+I);

				InputStream input = null;
				OutputStream output = null;
				try {
					input = new FileInputStream(f1);
					output = new FileOutputStream(arquivo);
					byte[] buf = new byte[102400];
					int bytesRead;
					while ((bytesRead = input.read(buf)) > 0) {
						output.write(buf, 0, bytesRead);}
				} finally {
					input.close();
					output.close();
				}
				F.add(arquivo);
				lista.add(I);
			}
		}catch (ConnectException e) {} 
		catch ( ClassNotFoundException e ) {} 
		catch(UnknownHostException unknownHost){}
		catch(IOException ioException){}
		finally{
			try{
				in.close();
				out.close();
				socketNo.close();
			}catch(IOException ioException){}
		}
	}
	

	private static class UploadHandler extends Thread {
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


	
	public static void MergeChunks() throws IOException {
		File f = new File("C:\\Users\\rish\\Desktop\\CN\\Client"+no+"\\"+sc);
		try{
			FileOutputStream out = new FileOutputStream(f,true);
			FileInputStream in = null;
			for(int i=0;i<lista.size();i++){
				int index = lista.indexOf(i+1);
				in = new FileInputStream(F.get(index));
				byte[] B = new byte[(int)F.get(index).length()];
				int b = in.read(B, 0,(int)F.get(index).length());
				out.write(B);
                out.flush();
				in.close();
			}
			out.close();
		}catch(Exception e){}
	}
        
public static void main (String args[])throws Exception{
		System.out.println("entra na Porta No. para esse cliente");
		Scanner scanner = new Scanner(System.in);
		String s1 = scanner.nextLine();
		clientePort = Integer.parseInt(s1);
		
		Client client = new Client();
		client.run();
		
		System.out.println("Recebendo meu compartilhando de chunks.");
		
		BootStrap BSH = new BootStrap();
		BSH.run();
		while(lista.size() != total || UNflag){ 
        	ServerSocket S1 = null;
			Thread t1 = null;
			Thread t2 = null;
			
			if(lista.size() != total){
				t2 = new DownloadHandler();
				t2.start();
			}
			
			if(UNflag){
        			try {	
							S1 = new ServerSocket(clientePort);
                			t1 = new UploadHandler(S1.accept());
					t1.start();
            				
        			} finally {
            				S1.close();
        			}
			}
	
			if(t1 != null){t1.join();}
			if(t2 != null){t2.join();}
		}
		
		MergeChunks();
		System.out.println("compartilhamento de  Chunks feitos.");
	}

}