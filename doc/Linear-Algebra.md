Linear Algebra
==============

Like [breeze](https://github.com/scalanlp/breeze), the target of linear algebra
library in scasci is to provide Matlab/NumPy like interfaces.

Core concept
------------

Like Matlab, both vector and matrix are treated in the same manner,
i.e. represented in class Matrix. Vector is treated as a special
matrix, i.e. nx1 matrix. Matrix storage is column major, while index
starting from 0.

Quick reference
---------------

The following tables assume that Numpy is used with `from numpy import
*` and Scasci with `import net.whily.scasci.math.linalg._`.

### Creation

| Operation | Scasci | Matlab | Numpy |
| --------- | ------ | ------ | ----- |
| Zeroed matrix | TODO  | `c = zeros(n,m)` | `c = zeros((n,m))` |
| Zeroed vector | TODO | `a = zeros(n)` | `a = zeros(n)` |
| Vector of ones | TODO | `a = ones(n)` | `a = ones(n)` |
| Vector of particular number | TODO | `a = ones(n) * 5` | `a = ones(n) * 5` |
| Identity matrix | TODO | `eye(n)` | `eye(n)` |
| Diagonal matrix |TODO | `diag([1 2 3])` | `diag((1,2,3))` |
| Matrix inline creation | TODO  | `a = [1 2; 3 4]` | `a = array([ [1,2], [3,4] ])` |
| Column vector inline creation | TODO | `a = [1 2 3 4]` | `a = array([1,2,3,4])` |
| Row  vector inline creation | TODO | `a = [1,2,3,4]'` | `a = array([1,2,3]).reshape(-1,1)` |

### Manipulation

| Operation | Scasci | Matlab | Numpy |
| --------- | ------ | ------ | ----- |
| Basic Indexing | `a(0,1)` | `a(1,2)` | `a[0,1]` |
| Extract subset of vector | TODO | `a(2:5)` | `a[1:4]` |
| Extract tail of vector | TODO | `a(2:end)` | `a[1:]` |
| Last element of vector | TODO | `a(end)` | `a[-1]` |
| Extract column of matrix | TODO | `a(:,3)` | `a[:,2]` |
| Reshaping | - | `reshape(a, n, m)` | `a.reshape(3,2)` |
| Flatten matrix | TODO | `a(:)` | `a.flatten()` |
| Copy lower triangle | TODO | `tril(a)` | `tril(a)` |
| Copy upper triangle | TODO | `triu(a)` | `triu(a)` |
| Create view of matrix diagonal | TODO | NA | `diagonal(a)` (Numpy >= 1.9)|
| Vector Assignment to subset | TODO | `a(2:5) = 5` | `a[1:4] = 5` |
| Vector Assignment to subset | TODO | `a(2:5) = [1 2 3]` | `a[1:4] = array([1,2,3])` |
| Matrix Assignment to subset | TODO | `a(2:4,2:4) = 5` | `a[1:3,1:3] = 5` |
| Matrix Assignment to column | TODO | `a(:,3) = 5` | `a[:,2] = 5` |
| Matrix vertical concatenate | TODO | `[a ; b]` | `vstack((a,b))` |
| Matrix horizontal concatenate | TODO | `[a , b]` | `hstack((a,b))` |
| Vector concatenate | TODO | `[a b]` | `concatenate((a,b))` |

### Operations

| Operation | Scasci | Matlab | Numpy |
| --------- | ------ | ------ | ----- |
| Elementwise addition | TODO | `a + b` | `a + b` |
| Elementwise multiplication | TODO | `a .* b` | `a * b` |
| Elementwise comparison | TODO | `a < b` (gives matrix of 1/0 instead of true/false)| `a < b` |
| Inplace addition | TODO | `a += 1` | `a += 1` |
| Inplace elementwise multiplication | TODO | `a *= 2` | `a *= 2` |
| Vector dot product | TODO | `dot(a, b)` | `dot(a, b)` |
| Elementwise sum | TODO | `sum(sum(a))` | `a.sum()` |
| Elementwise max | TODO | `max(a)` | `a.max()` |
| Elementwise argmax | TODO | `argmax(a)` | `a.argmax()` |
| Ceiling | TODO | `ceil(a)` | `ceil(a)` |
| Floor | TODO | `floor(a)` | `floor(a)` |

### Basic Functions

| Operation | Scasci | Matlab | Numpy |
| --------- | ------ | ------ | ----- |
| Sum down each column (giving a row vector) | TODO | `sum(a)` | `sum(a,0)` |
| Sum across each row (giving a column vector) | TODO | `sum(a')` | `sum(a,1)` |
| Linear solve | TODO | `a \ b` | `linalg.solve(a,b)` |
| Transpose | TODO | `a'` | `a.T` |
| Determinant | TODO | `det(a)` | `linalg.det(a)` |
| Inverse | TODO | `inv(a)` | `linalg.inv(a)` |
| Pseudoinverse | TODO | `pinv(a)` | `linalg.pinv(a)` |
| Norm | TODO | `norm(a)` | `norm(a)` |
| Eigenvalues (Symmetric) | TODO | `[v,l] = eig(a)` | `linalg.eig(a)[0]` |
| Eigenvalues | TODO | `eig(a)` | `linalg.eig(a)[0]` |
| Eigenvectors | TODO | `[v,l] = eig(a)` | `linalg.eig(a)[1]` |
| Singular values | TODO | `svd(a)` | `linalg.svd(a)` |
| Rank | TODO | `rank(a)` | `rank(a)` |
| Vector length | TODO | `size(a)` | `a.size` |
| Matrix rows | `a.rows` | `size(a, 1)` | `a.shape[0]` |
| Matrix columns | `a.cols` | `size(a, 2)` | `a.shape[1]` |
