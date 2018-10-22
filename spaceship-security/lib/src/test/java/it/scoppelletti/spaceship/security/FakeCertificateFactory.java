/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * - Dario Scoppelletti, 2018
 * Original repository: http://android.googlesource.com/platform/frameworks/base
 * Original file: master/keystore/java/android/security/keystore/
 *     AndroidKeyStoreKeyPairGeneratorSpi.java
 * Commit: b631503200c8de47bbd83a71f17c798f5c2f1582 - March 15, 2018
 * Based on method generateSelfSignedCertificateWithFakeSignature of the class
 * AndroidKeyStoreKeyPairGeneratorSpi.
 * Support only RSA algorithm.
 * Introduce spec parameter instead of mSpec instance field.
 */

package it.scoppelletti.spaceship.security;

import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.x509.V3TBSCertificateGenerator;

public final class FakeCertificateFactory {

    private FakeCertificateFactory() {
    }

    @SuppressWarnings("deprecation")
    public static X509Certificate create(PublicKey publicKey,
            FakeKeyPairGeneratorSpec spec) throws IOException,
            CertificateParsingException {
        ASN1ObjectIdentifier sigAlgOid;
        AlgorithmIdentifier sigAlgId;
        org.bouncycastle.jce.X509Principal subject;
        ASN1EncodableVector result;
        Certificate cert;
        org.bouncycastle.jce.provider.X509CertificateObject x509Cert;
        TBSCertificate tbsCertificate;
        ASN1InputStream publicKeyInfoIn = null;
        V3TBSCertificateGenerator tbsGenerator;
        byte[] signature;

        sigAlgOid = PKCSObjectIdentifiers.sha256WithRSAEncryption;
        sigAlgId = new AlgorithmIdentifier(sigAlgOid, DERNull.INSTANCE);
        signature = new byte[1];

        tbsGenerator = new V3TBSCertificateGenerator();
        try {
            publicKeyInfoIn = new ASN1InputStream(publicKey.getEncoded());
            tbsGenerator.setSubjectPublicKeyInfo(
                    SubjectPublicKeyInfo.getInstance(
                            publicKeyInfoIn.readObject()));
        } finally {
            if (publicKeyInfoIn != null) {
                publicKeyInfoIn.close();
            }
        }

        subject = new org.bouncycastle.jce.X509Principal(
                spec.getSubject().getEncoded());

        tbsGenerator.setSerialNumber(new ASN1Integer(spec.getSerialNumber()));
        tbsGenerator.setSubject(subject);
        tbsGenerator.setIssuer(subject);
        tbsGenerator.setStartDate(new Time(spec.getStartDate()));
        tbsGenerator.setEndDate(new Time(spec.getEndDate()));
        tbsGenerator.setSignature(sigAlgId);

        tbsCertificate = tbsGenerator.generateTBSCertificate();

        result = new ASN1EncodableVector();
        result.add(tbsCertificate);
        result.add(sigAlgId);
        result.add(new DERBitString(signature));

        cert = Certificate.getInstance(new DERSequence(result));
        x509Cert =
                new org.bouncycastle.jce.provider.X509CertificateObject(cert);
        return x509Cert;
    }
}
