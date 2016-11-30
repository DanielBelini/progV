package app;

import java.net.ServerSocket;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.io.*;
import java.net.Socket;
 
public class Server {

	public static ArrayList<File> F = new ArrayList<File>();
	public static int ChunkCnt = 0; 
	public static String sc; 

	public static void main(String[] args) throws Exception {
		System.out.println("O servidor esta rodando.");
		
		System.out.println("entre com o novo arquivo que deseja compartilhar.");
		Scanner scanner = new Scanner(System.in);
		sc = scanner.nextLine();
		fileChunks(new File("C:\\Users\\rish\\Desktop\\CN\\"+sc));
		
        ServerSocket listener = new ServerSocket(8000);
		int clienteNum = 1;
		
      	try {
           	while(true) {
             	new Handler(listener.accept(),clienteNum).start();
				System.out.println("Cliente "  + clienteNum + " esta conectado!");
				clienteNum++;
            }
        } finally {
          	listener.close();} 
	}
	
	public static void fileChunks(File arquivo) throws IOException {
        int chunkTamanho = 1024 * 100;
        byte[] buf = new byte[chunkTamanho];
	

		try {
			BufferedInputStream B = new BufferedInputStream(new FileInputStream(arquivo));
			int full = 0;
			while ((full = B.read(buf)) > 0){
				ChunkCnt  = ChunkCnt  + 1;
				String nome = "Chunk"+ChunkCnt;
				File novoArquivo = new File(arquivo.getParent(),nome);
         
				FileOutputStream out = new FileOutputStream(novoArquivo);
				out.write(buf, 0, full);
				System.out.println(novoArquivo.length());
				
				F.add(novoArquivo);
			}
		}catch(IOException e){}
	}
 
    private static class Handler extends Thread {
		private Socket conexao;
       	private ObjectInputStream in;
       	private ObjectOutputStream out;	
		private int no;	

        public Handler(Socket conecxao, int no) {
       		this.conexao = conexao;
    		this.no = no;
		}

        public void run() {
			try{
				out = new ObjectOutputStream(conexao.getOutputStream());
				out.flush();
				in = new ObjectInputStream(conexao.getInputStream());
				
				int limite = ChunkCnt/5;
				int temp = 4*limite;
				if(no == 5){
					limite = ChunkCnt - temp;
				}
				System.out.println("o limite do cliente é "+no+limite);
				
				sendMessage(""+no);
				sendMessage(sc);
				sendMessage(""+ChunkCnt);
				sendMessage(""+limite);
				
				int I = 3*(no-1);
				for(int i=0;i<limite;i++){
					sendFile(F.get(I));
					sendMessage(""+ ++I);	
				}
			}catch(IOException e){}
			finally{
				try{
					in.close();
					out.close();
					conexao.close();
				}catch(IOException e){}
			}
		}
		
		public void sendFile(File f){
			try{
				out.writeObject(f);
				out.flush();
				System.out.println("Send File: " + " to Client " + no);
			}catch(IOException e){}
		}
		
		public void sendMessage(String msg){
			try{
				out.writeObject(msg);
				out.flush();
				System.out.println("Send message: " + msg + " to Client " + no);
			}catch(IOException e){}
		}

    }

}
