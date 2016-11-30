/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import static app.Client.MergeChunks;
import static app.Client.N;
import static app.Client.UNflag;
import static app.Client.lista;
import static app.Client.total;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


class BootStrap{
		private ObjectInputStream inB;
		private ObjectOutputStream outB;
                private static int clientePort;	

		public BootStrap() {}
	
		public void run() {
			try{
				Socket rs = new Socket("localhost", 7000);
				System.out.println("Connectado na porta 7000");
			
				outB = new ObjectOutputStream(rs.getOutputStream());
				outB.flush();
				inB = new ObjectInputStream(rs.getInputStream());
			
				sendMessage(""+clientePort);
				N = Integer.parseInt((String)inB.readObject());
			
				System.out.println("cliente na Porta No. do servidor BootStrap "+N);
			}catch ( ClassNotFoundException e ){}
			catch(IOException e){}
		}
	
		void sendMessage(String msg){
			try{
				outB.writeObject(msg);
				outB.flush();
			}catch(IOException ioException){
			ioException.printStackTrace();}	
		}
	}

	