using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;
using System.Text;

namespace CertificateUtils
{
    public class CertificateUtils
    {
        public static String GetCertificate()
        {
            var res = new Response();
            res.code = 0;
            try
            {
                List<string> listCert = new List<string>();
                X509Store x509Store = new X509Store(StoreName.My, StoreLocation.LocalMachine);
                x509Store.Open(OpenFlags.ReadOnly);
                X509Certificate2Collection numberCerts = (X509Certificate2Collection)x509Store.Certificates;
                foreach (X509Certificate2 cert in numberCerts)
                {
                    try
                    {
                        String base64 = Convert.ToBase64String(cert.Export(X509ContentType.Cert));
                        listCert.Add(base64);
                    }
                    catch (Exception)
                    {

                    }
                }
                res.code = 1;
                res.data = JsonConvert.SerializeObject(listCert);
            }
            catch (Exception ex)
            {
                res.data = ex.Message;
            }
            
            return JsonConvert.SerializeObject(res);
        }

        public static String SignHash(String hash, String serialNumber, String hashAlgorithm)
        {
            var res = new Response();
            res.code = 0;
            try
            {
                X509Store x509Store = new X509Store(StoreName.My, StoreLocation.LocalMachine);
                x509Store.Open(OpenFlags.ReadOnly);

                X509Certificate2Collection certificateCollection =
                    x509Store.Certificates.Find(X509FindType.FindBySerialNumber, serialNumber.ToLower(), false);

                if (certificateCollection.Count == 0)
                {
                    res.data = "Filter Serial: Nothing Found";
                }
                else
                {
                    var cert = certificateCollection[0];
                    //var cert = new X509Certificate2(@"E:\DESKTOP\Certificate Test\Certificate Test\doan_truong_giang.p12", "1");
                    RSACryptoServiceProvider rsaCryptoServiceProvider = (RSACryptoServiceProvider)cert.PrivateKey;
                    byte[] signHash = rsaCryptoServiceProvider.SignHash(Convert.FromBase64String(hash), CryptoConfig.MapNameToOID(hashAlgorithm));
                    res.code = 1;
                    res.data = Convert.ToBase64String(signHash);
                }
            }
            catch (Exception ex)
            {
                res.data = ex.Message;
            }

            return JsonConvert.SerializeObject(res);
        }
    }
}
