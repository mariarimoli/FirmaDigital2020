using System;
using System.Collections.Generic;
using System.Text;

namespace TestProject
{
    class Program
    {
        static void Main(string[] args)
        {
            //String listCert = CertificateUtils.CertificateUtils.GetCertificate();
            //Console.WriteLine(listCert);

            String serial = "500000019aa3067f9ae9f3992200000000019a";
            String hash = "L/Z1d+bXTlFhfTvfmsGGZH8/tMw=";
            String hashAlgorithm = "SHA1";

            CertificateUtils.CertificateUtils.SignHash(hash, serial, hashAlgorithm);
            Console.ReadKey();
        }
    }
}
