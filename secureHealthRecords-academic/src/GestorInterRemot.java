public interface GestorInterRemot extends java.rmi.Remote
{
  public org.jdom.Document procedure2( org.jdom.Document a ) throws java.rmi.RemoteException;
  public boolean pas4( org.jdom.Document c ) throws java.rmi.RemoteException;
  public boolean verPacMetge( String pacient , String metge ) throws java.rmi.RemoteException;
  public org.jdom.Document procedure3 ( String pacient , String metge ) throws java.rmi.RemoteException;
  public org.jdom.Document procedure5 ( String id_usuari ) throws java.rmi.RemoteException;
  public boolean pas4InserirVisita ( String a ) throws java.rmi.RemoteException;
  public org.jdom.Document retornaLlistatCIE () throws java.rmi.RemoteException;
  public org.jdom.Document getDadesAdminPacient ( String id_usuari_vull , String id_usuari_peticio ) throws java.rmi.RemoteException;

}
