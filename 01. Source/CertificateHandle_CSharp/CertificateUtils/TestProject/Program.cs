using System;
using System.Collections.Generic;
using System.Text;

namespace TestProject
{
    class Program
    {
        static void Main(string[] args)
        {
            String listCert = CertificateUtils.CertificateUtils.GetCertificate();
            Console.WriteLine(listCert);
            Console.ReadKey();
        }
    }
}
