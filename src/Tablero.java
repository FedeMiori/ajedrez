import tiposPieza.*;

public class Tablero {
    private final int ALTO_TABLERO = 8;
    private final int ANCHO_TABLERO = 8;

    Casilla[][] casillas = new Casilla[ALTO_TABLERO][ANCHO_TABLERO];

    public Tablero(){
        for (int i = 0; i < ALTO_TABLERO; i++) {
            for (int j = 0; j < ANCHO_TABLERO; j++) {
                casillas[i][j] = new Casilla( new Posicion(i,j) );
            }
        }
    }
    public Casilla getCasilla(char letra, int numero){
        String input = String.valueOf(letra) + String.valueOf(numero);
        return getCasilla( new Posicion( input ) );
    }

    public Casilla getCasilla(Posicion posicion){
        Casilla casilla = null;
        if( posicion.dentroLimites(ALTO_TABLERO, ANCHO_TABLERO))
            casilla = casillas[posicion.getPosX()][posicion.getPosY()];
        return casilla;
    }

    public boolean casillaVacia(Posicion posicion){
        return getCasilla(posicion).getPieza() == null;
    }

    public Pieza getPiezaEnCasilla(Posicion posicion){
        return getCasilla(posicion).getPieza();
    }

    /**
     * Metodo que dibuja un tablero muy detallado por la terminal
     */
    public void printTablero(){
        System.out.print("   ");
        for (int i = 0; i < ANCHO_TABLERO; i++) {
            System.out.print("  "+(char) ('a'+i)+" ");
        }
        System.out.println();

        for (int i = ALTO_TABLERO -1; i >=0; i--) {
            System.out.print("   ");
            for (int j = 0; j < ANCHO_TABLERO; j++) {
                System.out.print("+---");
            }
            System.out.println("+");

            System.out.print((i+1)+"  ");
            for (int j = 0; j < ANCHO_TABLERO; j++) {
                System.out.print("| "+casillas[j][i].toString()+" ");
            }
            System.out.println("|  "+(i+1));
        }
        System.out.print("   ");
        for (int j = 0; j < ANCHO_TABLERO; j++) {
            System.out.print("+---");
        }
        System.out.println("+");
        System.out.print("   ");
        for (int i = 0; i < ANCHO_TABLERO; i++) {
            System.out.print("  "+(char) ('a'+i)+" ");
        }
        System.out.println();
    }
    
    public void inicializar(){

        //Colocamos peones
        for (int i = 0; i < ANCHO_TABLERO; i++) {
            casillas[i][1].setPieza( new Peon(Color.blanco) );
            casillas[i][6].setPieza( new Peon(Color.negro) );
        }

        //Colocamos torres:

        getCasilla('a',1).setPieza( new Torre(Color.blanco) );
        getCasilla('h',1).setPieza( new Torre(Color.blanco) );
        getCasilla('a',8).setPieza( new Torre(Color.negro) );
        getCasilla('h',8).setPieza( new Torre(Color.negro) );

        //Colocamos caballos:
        getCasilla('b',1).setPieza( new Caballo(Color.blanco) );
        getCasilla('g',1).setPieza( new Caballo(Color.blanco) );
        getCasilla('b',8).setPieza( new Caballo(Color.negro) );
        getCasilla('g',8).setPieza( new Caballo(Color.negro) );

        //Alfiles
        getCasilla('c',1).setPieza( new Alfil(Color.blanco) );
        getCasilla('f',1).setPieza( new Alfil(Color.blanco) );
        getCasilla('c',8).setPieza( new Alfil(Color.negro) );
        getCasilla('f',8).setPieza( new Alfil(Color.negro) );

        //Reyes
        getCasilla('e',1).setPieza( new Rey(Color.blanco) );
        getCasilla('e',8).setPieza( new Rey(Color.negro) );

        //Reinas
        getCasilla('d',1).setPieza( new Reina(Color.blanco) );
        getCasilla('d',8).setPieza( new Reina(Color.negro) );


    }

    /**
     * Metodo que mueve la pieza situalda en posicionOrigen a la posicionDestino
     */
    public void moverPieza(Posicion posicionOrigen, Posicion posicionDestino){
        boolean autorizadoAMover = false;
        Pieza piezaAMover = getPiezaEnCasilla(posicionOrigen);
        Pieza piezaEnDestino = getPiezaEnCasilla(posicionDestino);
        int[] vectorMovimiento = posicionOrigen.getVector( posicionDestino );
        //primero comprobamos que ambas posiciones esten dentro del tablero para evitar errores
        if(posicionOrigen.dentroLimites(ALTO_TABLERO, ANCHO_TABLERO) && posicionDestino.dentroLimites(ALTO_TABLERO, ANCHO_TABLERO)){
            //Segundo comprueba que en la posicion selecionada haya una fichaa que mover
            //y se comprueba que el movimiento sea valido para esa pieza pasandole el vector del movimiento
            //Ej: una torre no acepta un movimiento diagonal
            if(piezaAMover != null && piezaAMover.movimientoValido(vectorMovimiento))
                //Tercero se comprueba que en la trayectoria entre orijen y destino no haya otras piezas
                //o en su defecto que la ficha a mover sea un caballo ya que puede saltar por encima de otras piezas
                if (caminoDespejado(posicionOrigen, posicionDestino) || piezaAMover instanceof Caballo)
                    //y por ultimo que la casilla destino este vacia o que la pieza sea del coloe opuesto
                    // ya que no se puede mover una pieza a una casilla donde hay otra pieza del mismo color
                    if (piezaEnDestino == null || piezaEnDestino.distintoColor(piezaAMover))
                        autorizadoAMover = true;
        }
        else if(piezaAMover instanceof Peon && piezaAMover.movimientoValido(vectorMovimiento, piezaEnDestino))
            autorizadoAMover = true;

        if(autorizadoAMover){
            piezaAMover.incrementarNumMovimientos();
            getCasilla(posicionOrigen).setPieza(null);
            getCasilla(posicionDestino).setPieza(piezaAMover);
        }else
            System.out.println("No se pudo realizar el movimiento");
    }

    /**
     * Metodo que indica si las casillas intermedias entre dos posiciones estan vacias
     * sirve para ver si una pieza que no sea un caballo tiene el camino despejado para autorizar el movimiento
     * @param origen posicion origen del movimiento
     * @param destino posicion ddestino del movimiento
     * @return devuelve true si la trayectoria entre los dos puntos no tiene piezas
     */
    public boolean caminoDespejado(Posicion origen, Posicion destino){
        int[] vector = origen.getVector(destino);
        boolean sinObstaculos = true;
        Posicion posicionIteradora = new Posicion( origen.getPosX(), origen.getPosY() );
        if( vector[0]*vector[1] == 0 || Math.abs(vector[0]) == Math.abs(vector[1]) ){
            for (int i = 0; i < vector.length; i++) {
                if(vector[i] < 0) vector[i] = -1;
                else if(vector[i] > 0) vector[i] = 1;
            }
            posicionIteradora.sumarVector( vector );
            while(!posicionIteradora.equals(destino)
                    && posicionIteradora.dentroLimites(ANCHO_TABLERO,ALTO_TABLERO)
                    && sinObstaculos){
                if(!casillaVacia(posicionIteradora)) {
                    sinObstaculos = false;
                }
                posicionIteradora.sumarVector(vector);
            }
        }
        return sinObstaculos;
    }
}
