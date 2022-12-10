import base64
import random
import string

import pyaes, pbkdf2, binascii, secrets

def generate_random_string(n = 10):
    return ''.join(random.choices(string.ascii_lowercase +
                             string.digits, k=n))
def getKey(rand_t):
    password = "s312323ihkk123"
    passwordSalt = '10982'
    key = pbkdf2.PBKDF2(password, passwordSalt).read(32)
    return key

def encrypt(plaintext, key):
    aes = pyaes.AESModeOfOperationCTR(key, pyaes.Counter(31129547035000047302952433967654195398124239844566322884172163637846056248223))
    ciphertext = aes.encrypt(plaintext)
    ciphertext = str(base64.b64encode(ciphertext), 'utf-8')
    return ciphertext

def decrypt(enc, key):
    aes = pyaes.AESModeOfOperationCTR(key, pyaes.Counter(31129547035000047302952433967654195398124239844566322884172163637846056248223))
    decrypted = aes.decrypt(enc)
    decrypted = decrypted.decode("utf-8")
    return decrypted


text = "hello world"
rand_text = generate_random_string()
key = getKey(rand_text)
print(key)
e1 = encrypt(text, key)
e2 = encrypt(text, key)
print(e1)
print(e2)


e1 = base64.b64decode(e1)
d1 = decrypt(e1, key)
# d2 = decrypt(e2)


# print(e2)
print(d1)


text = "big bang theory"
rand_text = generate_random_string()
key = getKey(rand_text)

e1 = encrypt(text, key)
e2 = encrypt(text, key)
print(e1)
print(e2)


e1 = base64.b64decode(e1)
d1 = decrypt(e1, key)
# d2 = decrypt(e2)


# print(e2)
print(d1)

