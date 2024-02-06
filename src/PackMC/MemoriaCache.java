package PackMC;
import java.util.Scanner;

/**
 * @author Casajus, Julen
 * @author Castelar, Ruben
 * @author Gonzalez, Iker
 */

public class MemoriaCache {
	
	
	public static void main(String[] args) {
		
		int Aux, TPalabra, TBloque, TConjunto, Palabras, Columnas, Direccion, TAcceso = 0, TTAccesos = 0;
		int PAbsoluta, Bloque, PEnBloque, Conjuntos, Tag, Conjunto, Referencias = 0, Aciertos = 0;
		int FirstBloqueMC, BloqueMC, BloquesMC, BloqueMCAntiguo = 0;
		boolean end = false;
		
		String TReemplazo = "", TOperacion, Aux1, Accesos = "\nTiempos de acceso: ";
		
		
		//INTERFAZ CONFIGURACION INICIAL
		Scanner scanner = new Scanner (System.in);
		
		System.out.println("¡Bienvenido al Simulador de una Memoria Cache! \nPulsa enter para comenzar");
		
		scanner.nextLine();
		
		do {
			System.out.println("\nSelecciona el tamaño de palabra (4 u 8 bytes): ");
			try {
				TPalabra = Integer.parseInt(scanner.nextLine());

                if (TPalabra != 4 && TPalabra != 8) {
                    throw new IllegalArgumentException();
                }
            } catch (NumberFormatException e) {
                System.out.println("Error al seleccionar tamaño. \n");
                TPalabra = 0;
            } catch (IllegalArgumentException e) {
                System.out.println("Error al seleccionar tamaño. \n");
                TPalabra = 0;
            }
		} while(TPalabra != 4 && TPalabra != 8);

		do {
			System.out.println("\nSelecciona el tamaño de bloque (32 o 64 bytes): ");
			try {
				TBloque = Integer.parseInt(scanner.nextLine());

                if (TBloque != 32 && TBloque != 64) {
                    throw new IllegalArgumentException();
                }
            } catch (NumberFormatException e) {
                System.out.println("Error al seleccionar tamaño. \n");
                TBloque = 0;
            } catch (IllegalArgumentException e) {
                System.out.println("Error al seleccionar tamaño. \n");
                TBloque = 0;
            }
		} while(TBloque != 32 && TBloque != 64);
		
		do {
			System.out.println("\nSelecciona el tamaño de conjunto (1 (directa), 2, 4 u 8 (totalmente asociativa)): ");
			try {
				TConjunto = Integer.parseInt(scanner.nextLine());

                if (TConjunto != 1 && TConjunto != 2 && TConjunto != 4 && TConjunto != 8) {
                    throw new IllegalArgumentException();
                }
            } catch (NumberFormatException e) {
                System.out.println("Error al seleccionar tamaño. \n");
                TConjunto = 0;
            } catch (IllegalArgumentException e) {
                System.out.println("Error al seleccionar tamaño. \n");
                TConjunto = 0;
            }
		} while(TConjunto != 1 && TConjunto != 2 && TConjunto != 4 && TConjunto != 8);
		 Conjuntos = 8 / TConjunto; //CONJUNTOS TOTALES EN MC

		
		if(TConjunto != 1) {
			do {
				System.out.println("\nSelecciona la politica de reemplazo (FIFO o LRU): ");
				try {
					TReemplazo = scanner.nextLine();
	
	                if (!TReemplazo.equals("FIFO") && !TReemplazo.equals("LRU")) {
	                    throw new IllegalArgumentException();
	                }
	            } catch (IllegalArgumentException e) {
	                System.out.println("Error al seleccionar politica, escriba FIFO o LRU. \n");
	                TReemplazo = "";
	            }
			} while(!TReemplazo.equals("FIFO") && !TReemplazo.equals("LRU"));
		}
		
		
		//CREAR MATRIZ DE LA MEMORIA CACHE
		Palabras = TBloque/TPalabra;
		Columnas = 4 + Palabras;
		
		if(TConjunto == 1) Columnas--;
		
		int [][] Cache = new int[8][Columnas];
		
		for(int i = 0; i < 8; i++) { //EVITA PROBLEMAS CON TAG 0
			Cache[i][2] = -1;
		}
		
		
		//PEDIR DIRECCIONES MIENTRAS EL PROGRAMA FUNCIONE
			while(!end) {
			
			do {
				System.out.println("\nIndica la direccion de memoria que quieres leer/escribir: ");
				try {
					Direccion = Integer.parseInt(scanner.nextLine());
	
	                if (Direccion < 0) {
	                    throw new IllegalArgumentException();
	                }
	            } catch (NumberFormatException e) {
	                System.out.println("Error al indicar direccion, escriba un numero positivo. \n");
	                Direccion = -1;
	            } catch (IllegalArgumentException e) {
	                System.out.println("Error al indicar direccion, escriba un numero positivo. \n");
	                Direccion = -1;
	            }
			} while(Direccion < 0);
			
			do {
				System.out.println("\nQue operacion deberia realizarse, lectura (ld) o escritura (st): ");
				try {
					TOperacion = scanner.nextLine();
	
	                if (!TOperacion.equals("ld") && !TOperacion.equals("st")) {
	                    throw new IllegalArgumentException();
	                }
	            } catch (IllegalArgumentException e) {
	                System.out.println("Error al indicar operacion, escriba ld o st. \n");
	                TOperacion = "";
	            }
			} while(!TOperacion.equals("ld") && !TOperacion.equals("st"));
						
			//INTERPRETACION DE LA DIRECCION
			PAbsoluta = Direccion / TPalabra;
			Bloque = PAbsoluta / Palabras;
			PEnBloque = PAbsoluta % Palabras;
			Tag = Bloque / Conjuntos;
			Conjunto = Bloque % Conjuntos;
			
			System.out.println("\nLa direccion " + Direccion + " representa:\n");
			System.out.println("Palabra absoluta: " + PAbsoluta);
			System.out.println("Bloque: " + Bloque);
			System.out.println("Palabra dentro de bloque: " + PEnBloque);
			System.out.println("Conjuntos totales en la memoria cache: " + Conjuntos);
			System.out.println("Tag en la memoria cache: " + Tag);
			System.out.println("Conjunto en la memoria cache: " + Conjunto);
			
			Referencias++;
			Accesos += "Busqueda Cache: 2 ciclos ";
			TAcceso += 2;
			
			//MISS/HIT, REEMPLAZO Y ACTUALIZAR LA MC
			if(TConjunto == 1) { //MC DIRECTA
				
				BloquesMC = Bloque % 8; //BLOQUE MC EN DIRECTA
				
				if(Cache[BloquesMC][2] == Tag) { //HIT
					System.out.println("\nEl bloque esta cargado en la MC, es un hit, en el bloque " + (BloquesMC));
					Aciertos++;
				} else { //MISS
					System.out.println("\nEl bloque no esta cargado en la MC, es un miss.");

					Accesos += ", Transferencia de Bloque (MP->MC): 21 ciclos + (" + (Palabras - 1) + " * 1 ciclo) ";
					TAcceso += 21 + (Palabras - 1);
					
					if(Cache[BloquesMC][0]==1) {
						System.out.println("\nEl bloque " + BloquesMC + " esta ocupado, por lo que se reemplaza.");
						if(Cache[BloquesMC][1]==1) {
							System.out.println("\nAdemás, el bloque " + BloquesMC + " esta modificado, por lo que habrá que transferirlo a la memoria principal antes de reemplazarlo.");
							
							Accesos += ", Transferencia de Bloque (MC->MP): 21 ciclos + (" + (Palabras - 1) + " * 1 ciclo) ";
							TAcceso += 21 + (Palabras - 1);
						}
					} else System.out.println("\nEl bloque " + BloquesMC + " esta libre, por lo que no se reemplaza.");
					
					//ACTUALIZAR LA MC
					Cache[BloquesMC][0] = 1;
					Cache[BloquesMC][2] = Tag;
				}
				
				//ACTUALIZAR LA MC
				if(TOperacion.equals("st")) Cache[BloquesMC][1] = 1;
				
			} else { //MC ASOCIATIVA
				
				FirstBloqueMC = Conjunto * TConjunto; //PRIMER BLOQUE DE CONJUNTO
				BloqueMC = -1;
				Aux = FirstBloqueMC;
				
				while(Aux < (FirstBloqueMC + TConjunto)) {
					if(Cache[Aux][2] == Tag) BloqueMC = Aux;
					if(Cache[Aux][3] == 0) BloqueMCAntiguo = Aux;
					Aux++;
				} Aux = FirstBloqueMC;
				
				if(BloqueMC == -1) { //MISS
					System.out.println("\nEl bloque no esta cargado en la MC, es un miss.");

					Accesos += ", Transferencia de Bloque (MP->MC): 21 ciclos + (" + (Palabras - 1) + " * 1 ciclo) ";
					TAcceso += 21 + (Palabras - 1);
					
					if(Cache[BloqueMCAntiguo][0] == 1) { //REEMPLAZO?
						System.out.println("\nEl bloque " + BloqueMCAntiguo%TConjunto + " del conjunto " + Conjunto + " esta ocupado, por lo que se reemplaza.");
						if(Cache[BloqueMCAntiguo][1] == 1) {
							System.out.println("\nAdemás, el bloque " + BloqueMCAntiguo%TConjunto + " del conjunto " + Conjunto  + " esta modificado, por lo que habrá que transferirlo a la memoria principal antes de reemplazarlo.");
							
							Accesos += ", Transferencia de Bloque (MP->MC): 21 ciclos + (" + (Palabras - 1) + " * 1 ciclo) ";
							TAcceso += 21 + (Palabras - 1);
						}
					} else System.out.println("\nEl bloque " + BloqueMCAntiguo%TConjunto + " del conjunto " + Conjunto + " esta libre, por lo que no se reemplaza.");	
					
					//ACTUALIZAR LA MC
					Cache[BloqueMCAntiguo][0] = 1;
					if(TOperacion.equals("st")) Cache[BloqueMCAntiguo][1] = 1;
					Cache[BloqueMCAntiguo][2] = Tag;
					
					while(Aux < (FirstBloqueMC + TConjunto)) {
						if(Aux != BloqueMCAntiguo && Cache[Aux][3] > Cache[BloqueMCAntiguo][3]) Cache[Aux][3]--;
						Aux++;
					} Cache[BloqueMCAntiguo][3] = TConjunto - 1;

				} else { //HIT
					System.out.println("\nEl bloque esta cargado en la MC, es un hit, en el bloque " + (BloqueMC%TConjunto) + " del conjunto " + Conjunto);
					Aciertos++;

					//ACTUALIZAR LA MC
					Cache[BloqueMC][0] = 1;
					if(TOperacion.equals("st")) Cache[BloqueMC][1] = 1;
					Cache[BloqueMC][2] = Tag;
					if(TReemplazo.equals("LRU")) {
						while(Aux < (FirstBloqueMC + TConjunto)) {
							if(Aux != BloqueMCAntiguo && Cache[Aux][3] > Cache[BloqueMCAntiguo][3]) Cache[Aux][3]--;
							Aux++;
						} Cache[BloqueMC][3] = TConjunto - 1;
					}
				}
			}
			
			TTAccesos += TAcceso;
			
			System.out.println(Accesos);
			System.out.println("\nEl tiempo de acceso es de " + TAcceso + " ciclos.\n");
			System.out.println("Referencias: " + Referencias);
			System.out.println("Aciertos: " + Aciertos);
			System.out.println("Tasa de aciertos: " + (float)Aciertos / Referencias);
			System.out.println("Tiempo total: " + TTAccesos);
			
			//PRINT LA MATRIZ
			System.out.print("\nLA MEMORIA CACHE QUEDA ASI:\n");
			System.out.print("\nOcup  Mod   Tag  ");
			if(TConjunto != 1) System.out.print(TReemplazo + "  ");
	
			for(int i = 0; i < 8; i++) {
				if(i % TConjunto == 0) System.out.println();
							
				for(int j = 0; j < (Columnas); j++) {
					System.out.print(Cache[i][j] + "     ");
					
				} System.out.println();
			}
			System.out.println("\n¿Continuar el programa? (y/n)");
			Aux1 = scanner.nextLine();
			if(Aux1.equals("n")) end = true;
			System.out.println();
		}
		scanner.close();
	}
}